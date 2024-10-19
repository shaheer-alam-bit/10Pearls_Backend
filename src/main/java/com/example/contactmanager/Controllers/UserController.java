package com.example.contactmanager.Controllers;

import com.example.contactmanager.DTO.*;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class UserController
{
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Testing API
    @GetMapping("/data")
    public ResponseEntity<String> getData() {
        return ResponseEntity.ok("Hello from Spring Boot");
    }
    

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> RegisterUser(@RequestBody User user)
    {
        return userService.createUser(user);
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
    public ResponseEntity<ChangePasswordResponse> changeMyPassword(@RequestBody ChangePasswordRequest changePasswordRequest)
    {
        String email = changePasswordRequest.getEmail();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        String confirmPassword = changePasswordRequest.getConfirmPassword();

        return userService.changePassword(email,oldPassword,newPassword,confirmPassword);
    }

}
