package com.example.contactmanager.controllers;

import com.example.contactmanager.dto.*;
import com.example.contactmanager.model.User;
import com.example.contactmanager.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class UserController
{
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/getDetails/{userId}")
    public ResponseEntity<User> getUserData(@PathVariable long userId) {
        return userService.getData(userId);
    }
    

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> registerUser(@Valid @RequestBody User user)
    {
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest)
    {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        return userService.verifyUser(email,password);
    }


    @PostMapping("/changePassword")
    public ResponseEntity<ChangePasswordResponse> changeMyPassword(@RequestBody ChangePasswordRequest changePasswordRequest)
    {
        String email = changePasswordRequest.getEmail();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        String confirmPassword = changePasswordRequest.getConfirmPassword();

        return userService.changePassword(email,oldPassword,newPassword,confirmPassword);
    }

}
