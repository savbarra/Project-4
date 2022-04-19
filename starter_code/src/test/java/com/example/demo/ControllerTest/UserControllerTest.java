package com.example.demo.ControllerTest;

import com.example.demo.Helper;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Autowired
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void init(){
        this.userController = new UserController();
        Helper.injectObjects(userController, "userRepository", userRepository);
        Helper.injectObjects(userController, "cartRepository", cartRepository);
        Helper.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void givenValidUserInfo_thenCreateUser(){
        //Given
        User newUser = Helper.getUser();
        when( bCryptPasswordEncoder.encode(newUser.getPassword()) ).thenReturn(Helper.ENCODED_PASSWORD);

        CreateUserRequest userRequest = new CreateUserRequest();

        userRequest.setUsername(newUser.getUsername());
        userRequest.setPassword(newUser.getPassword());
        userRequest.setConfirmPassword(newUser.getPassword());

        //Then
        ResponseEntity<User> response = userController.createUser(userRequest);
        Assert.assertNotNull(response);
        User userCreated = response.getBody();
        Assert.assertEquals(newUser.getUsername(), userCreated.getUsername());
        Assert.assertEquals(Helper.ENCODED_PASSWORD, userCreated.getPassword());
    }

    @Test
    public void givenUser_whenUserInfoValid_thenGetUserByUsername(){
        //Given
        User user = Helper.getUser();

        //When
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<User> responseFind = userController.findByUserName(user.getUsername());
        Assert.assertNotNull(responseFind);
        User userCreated = responseFind.getBody();

        //When
        Assert.assertEquals(user.getUsername(), userCreated.getUsername());
    }


    @Test
    public void givenUser_whenUserInfoValid_thenGetUserById(){
        //Given
        User user = Helper.getUser();
        Optional<User> optionalUser =Optional.of(user);

        //When
        when(userRepository.findById(user.getId())).thenReturn(optionalUser);
        ResponseEntity<User> responseFind = userController.findById(user.getId());
        Assert.assertNotNull(responseFind);
        User userCreated = responseFind.getBody();

        //When
        Assert.assertEquals(user.getUsername(), userCreated.getUsername());
    }

    @Test
    public void givenUser_whenPasswordInvalid_thenThrowBadRequest(){
        //Given
        User newUser = Helper.getUser();
        when( bCryptPasswordEncoder.encode(newUser.getPassword()) ).thenReturn(Helper.ENCODED_PASSWORD);

        CreateUserRequest userRequest = new CreateUserRequest();

        userRequest.setUsername(newUser.getUsername());
        userRequest.setPassword("11");
        userRequest.setConfirmPassword(newUser.getPassword());

        //Then
        ResponseEntity<User> response = userController.createUser(userRequest);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
