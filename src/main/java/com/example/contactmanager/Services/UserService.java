package com.example.contactmanager.Services;

import com.example.contactmanager.CustomExceptions.UserNotFoundException;
import com.example.contactmanager.DTO.ChangePasswordResponse;
import com.example.contactmanager.DTO.LoginResponse;
import com.example.contactmanager.DTO.SignupResponse;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService
{

    private final UserRepository userRepository;

   private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ResponseEntity<User> getData(long user_id)
    {
        User user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<SignupResponse> createUser(User user)
    {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
        {
            throw new IllegalArgumentException("Email is already in use.");
        }
           String hashedPassword = passwordEncoder.encode(user.getPassword());
           user.setPassword(hashedPassword);
           userRepository.save(user);
           log.info("New user created successfully");
           return new ResponseEntity<>(new SignupResponse("User Created Successfully",true), HttpStatus.CREATED);
    }

    public ResponseEntity<LoginResponse> verifyUser(String email, String password)
    {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User Not Found"
                ));
        if (user == null) {
            log.error("Email Not Found");
            return new ResponseEntity<>(new LoginResponse("Email does not exist",false), HttpStatus.NOT_FOUND);
        }
        else if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Wrong Password entered");
            return new ResponseEntity<>(new LoginResponse("Wrong password",false), HttpStatus.UNAUTHORIZED);
        }
        else {
            String generatedToken = jwtService.generateToken(user);
            log.info("Logged In Successfully");
            return new ResponseEntity<>(new LoginResponse(generatedToken,"User logged in successfully",true), HttpStatus.OK);
        }
    }

    public ResponseEntity<ChangePasswordResponse> changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User Not Found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.error("Old Password does not match");
            return new ResponseEntity<>(new ChangePasswordResponse("Wrong Old Password Entered",false), HttpStatus.UNAUTHORIZED);
        }

        if (!newPassword.equals(confirmPassword)) {
            log.error("New Password does not match");
            return new ResponseEntity<>(new ChangePasswordResponse("Passwords do not match",false), HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed Successfully");
        return new ResponseEntity<>(new ChangePasswordResponse("Password Changed Successfully",true), HttpStatus.OK);
    }

}
