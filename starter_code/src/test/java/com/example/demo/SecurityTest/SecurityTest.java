package com.example.demo.SecurityTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void givenURL_whenUnauthorized_thenReturnError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/cart")).andExpect(status().isForbidden());
    }

    @Test
    public void givenUser_WhenUserDoesNotExist_thenReturnError() throws Exception {
        String username = "NewUser1";
        String password = "SecretPassword";
        String body = "{\"username\":\"" + username + "\", \"password\":\" " + password + "\"}";

        mvc.perform(MockMvcRequestBuilders.post("/api/user/differentUser")
                        .content(body))
                .andExpect(status().isForbidden()).andReturn();
    }
}