package com.example.contactmanager.Services;

import com.example.contactmanager.CustomExceptions.UserNotFoundException;
import com.example.contactmanager.DTO.ChangePasswordResponse;
import com.example.contactmanager.DTO.LoginResponse;
import com.example.contactmanager.DTO.SignupResponse;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService
{
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    public ResponseEntity<SignupResponse> createUser(User user)
    {
           String hashedPassword = passwordEncoder.encode(user.getPassword());
           user.setPassword(hashedPassword);
           userRepository.save(user);
           return new ResponseEntity<>(new SignupResponse("User Created Successfully",true), HttpStatus.CREATED);
    }

    public ResponseEntity<LoginResponse> verifyUser(String email, String password)
    {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User Not Found"
                ));
        if (user == null) {
            return new ResponseEntity<>(new LoginResponse("Email does not exist",false), HttpStatus.NOT_FOUND);
        }
        else if (!passwordEncoder.matches(password, user.getPassword())) {
            return new ResponseEntity<>(new LoginResponse("Wrong password",false), HttpStatus.UNAUTHORIZED);
        }
        else {
            String generatedToken = jwtService.generateToken(user);
            return new ResponseEntity<>(new LoginResponse(generatedToken,"User logged in successfully",true), HttpStatus.OK);
        }
    }

    public ResponseEntity<List<User>> fetchAllUsers()
    {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    public ResponseEntity<ChangePasswordResponse> changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User Not Found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return new ResponseEntity<>(new ChangePasswordResponse("Wrong Old Password Entered",false), HttpStatus.UNAUTHORIZED);
        }

        if (!newPassword.equals(confirmPassword)) {
            return new ResponseEntity<>(new ChangePasswordResponse("Passwords do not match",false), HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new ResponseEntity<>(new ChangePasswordResponse("Password Changed Successfully",true), HttpStatus.OK);
    }

}
