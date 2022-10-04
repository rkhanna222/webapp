package com.cloud.rest.webservices.webapp;

import com.cloud.rest.webservices.webapp.models.User;
import com.cloud.rest.webservices.webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CourseCommandLineRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;



    @Override
    public void run(String... args) throws Exception {
        //userRepository.save(new User(new UUID(""),"raghav222.khanna@gmail.com","Raghav","Khanna","12345"));

    }
}
