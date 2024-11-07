package com.example.contactmanager.Controllers;

import com.example.contactmanager.DTO.*;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Services.UserService;
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

    @PostMapping("/getDetails/{user_id}")
    public ResponseEntity<User> getUserData(@PathVariable long user_id) {
        return userService.getData(user_id);
    }
    

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> RegisterUser(@Valid @RequestBody User user)
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
