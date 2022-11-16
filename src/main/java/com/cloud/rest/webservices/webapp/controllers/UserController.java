package com.cloud.rest.webservices.webapp.controllers;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.cloud.rest.webservices.webapp.errors.RegistrationStatus;
import com.cloud.rest.webservices.webapp.exception.UserNotFoundException;
import com.cloud.rest.webservices.webapp.models.User;
import com.cloud.rest.webservices.webapp.repositories.UserRepository;
import com.cloud.rest.webservices.webapp.services.UserServices;
import com.cloud.rest.webservices.webapp.validators.UserValidator;
import com.timgroup.statsd.StatsDClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.passay.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class UserController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private StatsDClient metricsClient;

    @Autowired
    private UserServices userServices;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UserValidator userValidator;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(userValidator);
    }

    public UserController(UserServices userServices,UserRepository userRepository) {
        this.userServices = userServices;
        this.userRepository = userRepository;
    }



    @GetMapping("/v1")
    public List<User> retrieveAllUsers(){
        return (List<User>) userRepository.findAll();
    }

    @GetMapping("/v1/account/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id, HttpServletRequest request) {
        metricsClient.incrementCounter("endpoint./v1/.account/.id.http.get");
        Optional<User> user = null;
        try {
            user = userRepository.findById(id);
            System.out.println(user);
        }
        catch (Exception e){
            LOGGER.warn("Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }

        

        String loggedUser = "";
        String username = "";
        String password = "";

        try {
            loggedUser = authenticatedUser(request);
            username = loggedUser.split(" ")[0];
            password = loggedUser.split(" ")[1];
        }
        catch(Exception e){
            LOGGER.warn("Unauthorized to access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Error");
        }

        if(user.get().isVerified() == false){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not verified");
        }

        if(!((user.get().getUsername().equals(username)) && (passwordEncoder.matches(password,user.get().getPassword())))){
            LOGGER.warn("Forbidden to access");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden to access");
        }
        if(user.isEmpty()){
            LOGGER.warn("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        LOGGER.info("User Found");
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    //Post User
    @PostMapping("/v1/account")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult errors, HttpServletResponse response) throws Exception {
        RegistrationStatus registrationStatus;

        metricsClient.incrementCounter("endpoint./v1/.account.http.post");
        if(user.getId()!=null){
            if(!user.getId().toString().isEmpty()){
                LOGGER.warn("Bad Request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot put ID");
            }
        }
        if(user.getAccountCreated()!=null){
            if(!user.getAccountCreated().toString().isEmpty()){
                LOGGER.warn("Bad Request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot put account created date");
            }
        }
        if(user.getAccountUpdated()!=null){
            if(!user.getUsername().isEmpty()){
                LOGGER.warn("Bad Request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot put account updated date");
            }
        }

        if(errors.hasErrors()) {
            registrationStatus = userServices.getRegistrationStatus(errors);
            LOGGER.warn("Bad Request " + registrationStatus);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationStatus);
        }else {
            LOGGER.info("User created successfully");
            AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

            CreateTopicResult topicResult = snsClient.createTopic("verify_user");
            String topicArn = topicResult.getTopicArn();

            final PublishRequest publishRequest = new PublishRequest(topicArn, user.getUsername());
            LOGGER.info("Verify request made "+publishRequest.getMessage());
            final PublishResult publishResponse = snsClient.publish(publishRequest);
            LOGGER.info("publishResponse " + publishResponse.toString());
            //return ResponseEntity.status(HttpStatus.CREATED).body("");
            return ResponseEntity.status(HttpStatus.CREATED).body(userServices.register(user));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String email, @RequestParam String token) throws Exception{
        metricsClient.incrementCounter("endpoint./verify.http.get");

        User user = userRepository.findByUsername(email);
        if(email==null || token==null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"token not found\"}");
        LOGGER.info("email " + email);
        if(user.isVerified()==true){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Verified Already");
        }
        else{
            if(isTokenValid(user.getUsername())) {
                userServices.update(email);
                return ResponseEntity.status(HttpStatus.OK).body("Verified Successfully");
            }
            else{
                return ResponseEntity.status(HttpStatus.OK).body("Link got expired!!");
            }
        }
    }

    @PutMapping("/v1/account/{id}")
    public ResponseEntity<?> updateUserDetails(@RequestBody User user,@PathVariable("id") UUID id,
                                             HttpServletRequest request) {

        metricsClient.incrementCounter("endpoint./v1/.account.id.http.put");

        Optional<User> u = null;
        try {
            u = userRepository.findById(id);
            System.out.println(user);
        }
        catch (Exception e){
            LOGGER.warn("Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }

        String loggedUser = "";
        String username = "";
        String password = "";

        try {
            loggedUser = authenticatedUser(request);
            username = loggedUser.split(" ")[0];
            password = loggedUser.split(" ")[1];
        }
        catch(Exception e){
            LOGGER.warn("UNAUTHORIZED Request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Error");
        }

        if(u.get().isVerified() == false){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not verified");
        }

        if(!((u.get().getUsername().equals(username)) && (passwordEncoder.matches(password,u.get().getPassword())))){
            LOGGER.warn("Forbidden to access");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden to access");
        }
        if(user==null){
            LOGGER.warn("Request body cannot be empty");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request body cannot be empty");
        }

        if(user.getUsername()!=null){
            if(!user.getUsername().isEmpty()){
                LOGGER.warn("BAD_REQUEST");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot update username");
            }
        }
        if(user.getPassword()!=null){
            if(!user.getPassword().isEmpty()){
                if(passwordValidate(user.getPassword())){
                    u.get().setPassword(passwordEncoder.encode(user.getPassword()));
                }
                else {
                    LOGGER.warn("BAD_REQUEST");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Password must 9-30 characters long and must have Uppercase, " +
                                    "Lowercase, Special characters and Digits\"");
                }
            }

        }
        if(user.getFirstName()!=null){
            if(!user.getFirstName().isEmpty()){
                u.get().setFirstName(user.getFirstName());
            }
        }

        if(user.getLastName()!=null){
            if(!user.getLastName().isEmpty()){
                u.get().setLastName(user.getLastName());
            }
        }
        if(user.getAccountCreated()!=null){
            if(!user.getAccountCreated().toString().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot put account created date");
            }
        }
        if(user.getAccountUpdated()!=null){
            if(!user.getUsername().isEmpty()){
                LOGGER.warn("BAD_REQUEST");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot put account updated date");
            }
        }
        u.get().setAccountUpdated(LocalDateTime.now());
        LOGGER.info("User data updated successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(u.get()));
    }




    private String authenticatedUser(HttpServletRequest request){

        String tokenEnc = request.getHeader("Authorization").split(" ")[1];
        byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);

        String userName = decodedStr.split(":")[0];
        String passWord = decodedStr.split(":")[1];
        System.out.println("Value of Token" + " "+ decodedStr);

        return (userName + " " + passWord);

    }

    private boolean passwordValidate(String password){

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(9, 30),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule()));
        RuleResult result = validator.validate(new PasswordData(password));

        return result.isValid();

    }

    private boolean isTokenValid(String email){

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new DefaultAWSCredentialsProviderChain()).build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("csye6225");

        long currentEpochTime= System.currentTimeMillis() / 1000L;

        GetItemSpec i_spec = new GetItemSpec()
                .withPrimaryKey("emailId",email)
                .withProjectionExpression("ttl_timestamp")
                .withConsistentRead(true);

        Item count = table.getItem(i_spec);
        LOGGER.info("Item in dynamo db " + count.toJSONPretty());
        try {
            if (Objects.nonNull(count)){
                if(currentEpochTime < Long.parseLong(count.toString())){

                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch(Exception e){
            LOGGER.warn(e.toString());
        }
        return false;
    }



}
