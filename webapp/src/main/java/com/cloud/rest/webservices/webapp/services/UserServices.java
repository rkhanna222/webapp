package com.cloud.rest.webservices.webapp.services;

import com.cloud.rest.webservices.webapp.errors.RegistrationStatus;
import com.cloud.rest.webservices.webapp.models.User;
import com.cloud.rest.webservices.webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Component
public class UserServices {
    private static List<User> users = new ArrayList<>();
    private static int countId = 0;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;



    public List<User> findAll(){
        return users;
    }

    public User getUserById(UUID id){
        Predicate<? super User> predicate = user -> user.getId().equals(id);
        return users.stream().filter(predicate).findFirst().orElse(null);
    }

    public void deleteUserById(int id){
        Predicate<? super User> predicate = user -> user.getId().equals(id);
        users.removeIf(predicate);
        //return users.stream().filter(predicate).findFirst().orElse(null);
    }


    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(LocalDateTime.now());
        return user;
    }

    public RegistrationStatus getRegistrationStatus(BindingResult errors) {
        FieldError emailIdError = errors.getFieldError("emailId");
        FieldError passwordError = errors.getFieldError("password");
        String emailIdErrorMessage = emailIdError == null ? "-" : emailIdError.getCode();
        String passwordErrorMessage = passwordError == null ? "-" : passwordError.getCode();
        return new RegistrationStatus(emailIdErrorMessage, passwordErrorMessage);
    }

}
