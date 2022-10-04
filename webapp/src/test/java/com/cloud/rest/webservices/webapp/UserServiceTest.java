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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class UserServiceTest {
    @MockBean
    UserServices userServicesTest;

    @MockBean
    UserRepository userRepositoryTest;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    private static User USER;

    @Before
    public void setUp() {
        this.USER = new User(UUID.randomUUID(), "rkKhanna@gmail.com", "Raghav","Khanna","P@$$W0rd123", LocalDateTime.now(),LocalDateTime.now());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void registerTest() {
        userServicesTest.register(USER);
        Mockito.verify(userRepositoryTest).save(USER);
        System.out.println("MockValue " + Mockito.verify(userRepositoryTest).save(USER));
    }

    @Test
    public void registerUserTest() throws Exception {

         Mockito.when(userServicesTest.register(Mockito.any(User.class)))
                 .thenReturn(USER);
         //Send course as body to /students/Student1/courses
         RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/account")
//         .accept(MediaType.APPLICATION_JSON).content(USER)
         .contentType(MediaType.APPLICATION_JSON);
         MvcResult result = mockMvc.perform(requestBuilder).andReturn();
         MockHttpServletResponse response = result.getResponse();
         assertEquals(HttpStatus.CREATED.value(), response.getStatus());
         //assertEquals("http://localhost:3030/v1/account",response.getHeader(HttpHeaders.LOCATION));
        }

}
