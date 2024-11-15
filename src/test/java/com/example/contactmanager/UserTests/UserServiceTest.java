package com.example.contactmanager.UserTests;


import com.example.contactmanager.customexceptions.UserNotFoundException;
import com.example.contactmanager.dto.ChangePasswordResponse;
import com.example.contactmanager.dto.LoginResponse;
import com.example.contactmanager.dto.SignupResponse;
import com.example.contactmanager.model.User;
import com.example.contactmanager.repositories.UserRepository;
import com.example.contactmanager.services.JwtService;
import com.example.contactmanager.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest
{
    @InjectMocks
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @Mock
    UserRepository userRepository;

    @Test
    void getUserDataTest() {
        long id = 123;
        User user = new User("Shaheer", "Alam", "shaheeralam.alam@gmail.com", "Shaheer.123");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        ResponseEntity<User> expectedResponse = new ResponseEntity<>(user, HttpStatus.OK);
        assertEquals(expectedResponse, userService.getData(id));
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        User user = new User("Shaheer", "Alam", "shaheeralam.alam@gmail.com", "Shaheer.123");
        String hashedPassword = "hashedPassword123";

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<SignupResponse> response = userService.createUser(user);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User Created Successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testVerifyUser_SuccessfulLogin() {
        // Arrange
        String email = "shaheeralam.alam@gmail.com";
        String password = "Shaheer.123";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);
        String generatedToken = "myJwtToken";

        User user = new User("Shaheer", "Alam", email, hashedPassword);


        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(generatedToken);

        ResponseEntity<LoginResponse> response = userService.verifyUser(email, password);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(generatedToken, response.getBody().getToken());
        assertEquals("User logged in successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testVerifyUser_IncorrectPassword() {
        // Arrange
        String email = "shaheeralam.alam@gmail.com";
        String password = "wrongPassword";
        String hashedPassword = "hashedPassword123";

        User user = new User("Shaheer", "Alam", email, hashedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        ResponseEntity<LoginResponse> response = userService.verifyUser(email, password);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wrong password", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testChangePassword_Success() {
        String email = "john.doe@example.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        User user = new User("John", "Doe", email, passwordEncoder.encode(oldPassword));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        ResponseEntity<ChangePasswordResponse> response = userService.changePassword(email, oldPassword, newPassword, confirmPassword);

        assertEquals("Password Changed Successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testChangePassword_UserNotFound() {
        String email = "john.doe@example.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(email, oldPassword, newPassword, confirmPassword));

        assertEquals("User Not Found", exception.getMessage());
    }

    @Test
    void testChangePassword_WrongOldPassword() {

        String email = "john.doe@example.com";
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        User user = new User("John", "Doe", email, "encodedOldPassword");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

        ResponseEntity<ChangePasswordResponse> response = userService.changePassword(email, oldPassword, newPassword, confirmPassword);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Wrong Old Password Entered", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testChangePassword_PasswordsDoNotMatch() {

        String email = "john.doe@example.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "differentPassword";

        User user = new User("John", "Doe", email, "encodedOldPassword");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        ResponseEntity<ChangePasswordResponse> response = userService.changePassword(email, oldPassword, newPassword, confirmPassword);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Passwords do not match", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }
}

