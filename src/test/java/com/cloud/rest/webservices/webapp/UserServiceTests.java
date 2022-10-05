package com.cloud.rest.webservices.webapp;

import com.cloud.rest.webservices.webapp.controllers.UserController;
import com.cloud.rest.webservices.webapp.models.User;
import com.cloud.rest.webservices.webapp.repositories.UserRepository;
import com.cloud.rest.webservices.webapp.services.UserServices;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {
    @MockBean
    UserServices userServicesTest;

    @MockBean
    UserRepository userRepositoryTest;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    private static User USER;

    String requestBody = """
            
                   {
                           "username": "sample@gmail.com",
                           "first_name": "Sample",
                           "password": "Raghav@1993",
                           "last_name": "Rannn"
                        
                   }
""";


    @Test
    public void registerUserTest() throws Exception {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

         Mockito.when(userServicesTest.register(Mockito.any(User.class)))
                 .thenReturn(USER);
         RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/account")
         .accept(MediaType.APPLICATION_JSON).content(requestBody)
         .contentType(MediaType.APPLICATION_JSON);
         MvcResult result = mockMvc.perform(requestBuilder).andReturn();
         MockHttpServletResponse response = result.getResponse();
         assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        }

}
