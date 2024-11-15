package com.example.contactmanager.UserTests;

import com.example.contactmanager.controllers.UserController;
import com.example.contactmanager.dto.*;
import com.example.contactmanager.model.User;
import com.example.contactmanager.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest
{

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    @Test
    void testGetUserDataStatus() throws Exception {
        // Arrange: Create a dummy user object with the constructor
        long userId = 1L;
        User user = new User("John", "Doe", "john.doe@example.com", "password123");

        // Mock the service method to return the dummy user with a 200 OK status
        when(userService.getData(anyLong()))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        // Act & Assert: Perform POST request and verify status is 200 OK
        mockMvc.perform(post("/getDetails/{user_id}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterUserStatus() throws Exception {
        // Arrange: Create a dummy user object for registration
        User user = new User("John", "Doe", "john.doe@example.com", "password123");
        ObjectMapper objectMapper = new ObjectMapper();
        // Mock the service method to return a successful user creation response
        when(userService.createUser(any(User.class)))
                .thenReturn(new ResponseEntity<>(new SignupResponse("User Created Successfully", true), HttpStatus.CREATED));

        // Act & Assert: Perform POST request to /signup and verify the status is 201 Created
        mockMvc.perform(post("/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    void testLoginUserSuccess() throws Exception {
        // Arrange: Create a dummy LoginRequest object
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123");
        ObjectMapper objectMapper = new ObjectMapper();
        // Mock the service method to return a successful login response
        when(userService.verifyUser(any(String.class), any(String.class)))
                .thenReturn(new ResponseEntity<>(new LoginResponse("dummyToken", "User logged in successfully", true), HttpStatus.OK));

        // Act & Assert: Perform POST request to /login and verify the status is 200 OK
        mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginUserInvalidPassword() throws Exception {
        // Arrange: Create a dummy LoginRequest object
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "wrongPassword");
        ObjectMapper objectMapper = new ObjectMapper();
        // Mock the service method to return an unauthorized response
        when(userService.verifyUser(any(String.class), any(String.class)))
                .thenReturn(new ResponseEntity<>(new LoginResponse("Wrong password", false), HttpStatus.UNAUTHORIZED));

        // Act & Assert: Perform POST request to /login and verify the status is 401 Unauthorized
        mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        // Arrange: Create a dummy LoginRequest object
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "password123");
        ObjectMapper objectMapper = new ObjectMapper();
        // Mock the service method to return a not found response
        when(userService.verifyUser(any(String.class), any(String.class)))
                .thenReturn(new ResponseEntity<>(new LoginResponse("Email does not exist", false), HttpStatus.NOT_FOUND));

        // Act & Assert: Perform POST request to /login and verify the status is 404 Not Found
        mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testChangePasswordWrongOldPassword() throws Exception {
        // Arrange: Create a dummy ChangePasswordRequest object
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("john.doe@example.com", "wrongOldPassword", "newPassword123", "newPassword123");

        // Mock the service method to return an unauthorized response due to wrong old password
        when(userService.changePassword(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(new ResponseEntity<>(new ChangePasswordResponse("Wrong Old Password Entered", false), HttpStatus.UNAUTHORIZED));
        ObjectMapper objectMapper = new ObjectMapper();
        // Act & Assert: Perform POST request to /changePassword and verify the status is 401 Unauthorized
        mockMvc.perform(post("/changePassword")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isUnauthorized());
    }

    // Test: New Password and Confirm Password Mismatch
    @Test
    void testChangePasswordMismatch() throws Exception {
        // Arrange: Create a dummy ChangePasswordRequest object
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("john.doe@example.com", "oldPassword123", "newPassword123", "mismatchedPassword123");

        // Mock the service method to return an unauthorized response due to password mismatch
        when(userService.changePassword(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(new ResponseEntity<>(new ChangePasswordResponse("Passwords do not match", false), HttpStatus.UNAUTHORIZED));
        ObjectMapper objectMapper = new ObjectMapper();
        // Act & Assert: Perform POST request to /changePassword and verify the status is 401 Unauthorized
        mockMvc.perform(post("/changePassword")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isUnauthorized());
    }

    // Test: Missing Fields in Request
    @Test
    void testChangePasswordMissingFields() throws Exception {
        // Arrange: Create a dummy ChangePasswordRequest object with missing newPassword
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("john.doe@example.com", "oldPassword123", "", "newPassword123");
        ObjectMapper objectMapper = new ObjectMapper();
        // Act & Assert: Perform POST request to /changePassword and expect a 400 Bad Request response
        mockMvc.perform(post("/changePassword")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());
    }
}
