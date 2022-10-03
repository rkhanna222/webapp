package com.cloud.rest.webservices.webapp;

import com.cloud.rest.webservices.webapp.models.User;
import com.cloud.rest.webservices.webapp.repositories.UserRepository;
import com.cloud.rest.webservices.webapp.services.UserServices;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

@RunWith(SpringRunner.class)
public class UserServiceTest {
    @InjectMocks
    UserServices userServicesTest;

    @Mock
    UserRepository userRepositoryTest;

    @Mock
    BCryptPasswordEncoder pwdEncoder;

    private static User USER;

    @Before
    public void setUp() {
        this.USER = new User(UUID.randomUUID(), "rkKhanna@gmail.com", "Raghav","Khanna","P@$$W0rd123", LocalDateTime.now(),LocalDateTime.now());
    }

    @Test
    public void registerTest() {
        userServicesTest.register(USER);
        Mockito.verify(userRepositoryTest).save(USER);
    }
}
