package com.cloud.rest.webservices.webapp.services;

import com.cloud.rest.webservices.webapp.errors.RegistrationStatus;
import com.cloud.rest.webservices.webapp.models.CustomUserDetails;
import com.cloud.rest.webservices.webapp.models.User;
import com.cloud.rest.webservices.webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Service
public class UserServices implements UserDetailsService{
    private static List<User> users = new ArrayList<>();
    private static int countId = 0;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public Boolean isEmailPresent(String username) {
        return userRepository.isEmailPresent(username) > 0 ? true : false;
    }


    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }

    public RegistrationStatus getRegistrationStatus(BindingResult errors) {
        FieldError usernameError = errors.getFieldError("username");
        FieldError passwordError = errors.getFieldError("password");
        FieldError firstnameError = errors.getFieldError("firstName");
        FieldError lastnameError = errors.getFieldError("lastName");
        String firstnameErrorMessage = firstnameError == null ? "-" : firstnameError.getCode();
        String lastnameErrorMessage = lastnameError == null ? "-" : lastnameError.getCode();
        String usernameErrorMessage = usernameError == null ? "-" : usernameError.getCode();
        String passwordErrorMessage = passwordError == null ? "-" : passwordError.getCode();
        return new RegistrationStatus(usernameErrorMessage, passwordErrorMessage,firstnameErrorMessage,lastnameErrorMessage);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user==null) throw new UsernameNotFoundException("User with given emailId does not exist");
        else return new CustomUserDetails(user);
    }
}
