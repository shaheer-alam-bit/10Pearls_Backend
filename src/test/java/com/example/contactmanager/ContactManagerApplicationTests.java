package com.example.contactmanager;

import com.example.contactmanager.DTO.ContactCreateResponse;
import com.example.contactmanager.DTO.ContactListResponse;
import com.example.contactmanager.DTO.LoginResponse;
import com.example.contactmanager.DTO.SignupResponse;
import com.example.contactmanager.Model.ContactDetails;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Repositories.ContactDetailsRepository;
import com.example.contactmanager.Repositories.UserRepository;
import com.example.contactmanager.Services.ContactDetailsService;
import com.example.contactmanager.Services.JwtService;
import com.example.contactmanager.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

@SpringBootTest
class ContactManagerApplicationTests {



	@Test
	void contextLoads() {
	}



}
