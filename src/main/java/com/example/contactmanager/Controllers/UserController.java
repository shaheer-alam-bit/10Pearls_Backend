package com.example.contactmanager.Controllers;

import com.example.contactmanager.DTO.ChangePasswordRequest;
import com.example.contactmanager.DTO.LoginRequest;
import com.example.contactmanager.DTO.LoginResponse;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class UserController
{
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> RegisterUser(@RequestBody User user)
    {
        userService.createUser(user);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED); // Return success message with 201 status
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> LoginUser(@RequestBody LoginRequest loginRequest)
    {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        return userService.verifyUser(email,password);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers()
    {
        return userService.fetchAllUsers();
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changeMyPassword(@RequestBody ChangePasswordRequest changePasswordRequest)
    {
        String email = changePasswordRequest.getEmail();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        String confirmPassword = changePasswordRequest.getConfirmPassword();

        return userService.changePassword(email,oldPassword,newPassword,confirmPassword);
    }

}
