package com.cloud.rest.webservices.webapp.controllers;

import com.cloud.rest.webservices.webapp.errors.RegistrationStatus;
import com.cloud.rest.webservices.webapp.exception.UserNotFoundException;
import com.cloud.rest.webservices.webapp.models.User;
import com.cloud.rest.webservices.webapp.repositories.UserRepository;
import com.cloud.rest.webservices.webapp.services.UserServices;
import com.cloud.rest.webservices.webapp.validators.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.passay.*;
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

        String loggedUser = "";

        try {
             loggedUser = authenticatedUser(request);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Error");
        }

        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if(!user.get().getUsername().equals(loggedUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to access");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }



    //Post User
    @PostMapping("/v1/account")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult errors, HttpServletResponse response) throws Exception {
        RegistrationStatus registrationStatus;

        System.out.println(user);

        if(user.getId()!=null){
            System.out.println("Inside first if");
            if(!user.getId().toString().isEmpty()){
                System.out.println("Inside Second if");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot put ID");
            }
        }
        if(user.getAccountCreated()!=null){
            if(!user.getAccountCreated().toString().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot put account created date");
            }
        }
        if(user.getAccountUpdated()!=null){
            if(!user.getUsername().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot put account updated date");
            }
        }

        if(errors.hasErrors()) {
            registrationStatus = userServices.getRegistrationStatus(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationStatus);
        }else {
            return ResponseEntity.status(HttpStatus.CREATED).body(userServices.register(user));
        }
    }

    @PutMapping("/v1/account/{id}")
    public ResponseEntity<?> updateUserDetails(@RequestBody User user,@PathVariable("id") UUID id,
                                             HttpServletRequest request) {

        String loggedUser = authenticatedUser(request);
        Optional<User> u = userRepository.findById(id);



        if(!u.get().getUsername().equals(loggedUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden to access");
        }

        System.out.println(user);
        if(user==null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request body cannot be empty");
        }

        if(user.getUsername()!=null){
            if(!user.getUsername().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot update username");
            }
        }

        if(user.getPassword()!=null){
            if(!user.getPassword().isEmpty()){
                if(passwordValidate(user.getPassword())){
                    u.get().setPassword(passwordEncoder.encode(user.getPassword()));
                }
                else {
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot put account updated date");
            }
        }
        u.get().setAccountUpdated(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(u.get()));
    }




    private String authenticatedUser(HttpServletRequest request){

        String tokenEnc = request.getHeader("Authorization").split(" ")[1];
        byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);

        String userName = decodedStr.split(":")[0];
        String passWord = decodedStr.split(":")[1];
        System.out.println("Value of Token" + " "+ decodedStr);

        return userName;

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



}
