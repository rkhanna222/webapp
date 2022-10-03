package com.cloud.rest.webservices.webapp.controllers;

import com.cloud.rest.webservices.webapp.errors.ErrorMessage;
import com.cloud.rest.webservices.webapp.errors.RegistrationStatus;
import com.cloud.rest.webservices.webapp.exception.UserNotFoundException;
import com.cloud.rest.webservices.webapp.models.User;
import com.cloud.rest.webservices.webapp.repositories.UserRepository;
import com.cloud.rest.webservices.webapp.services.UserServices;
import com.cloud.rest.webservices.webapp.validators.UserValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private UserRepository userRepository;


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
    public EntityModel<User> getUserById(@PathVariable UUID id) {

        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()){
            throw new UserNotFoundException("id:"+id);
        }

        EntityModel<User> entityModel = EntityModel.of(user.get());
        return entityModel;
    }



    //Post User
    @PostMapping("/v1/account")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult errors, HttpServletResponse response) throws Exception {
        RegistrationStatus registrationStatus;
        if(errors.hasErrors()) {
            registrationStatus = userServices.getRegistrationStatus(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationStatus);
        }else {
            return ResponseEntity.status(HttpStatus.CREATED).body(userServices.register(user));
        }
    }

//    @PutMapping("/v1/account/{id}")
//    public ResponseEntity<?> updateUserDetails(@Valid @RequestBody User user,@PathVariable("id") UUID id,
//    BindingResult errors, HttpServletResponse response) throws Exception {
//
//        //this.userServices.updateUserDetails(user,id);
//    }



}
