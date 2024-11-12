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

	@Autowired
	UserService userService;

	@Autowired
	ContactDetailsService contactDetailsService;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	JwtService jwtService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	ContactDetailsRepository contactDetailsRepository;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this); // Initializes mocks and injects into userService
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void getUserDataTest() {
		long id = 123;
		User user = new User("Shaheer", "Alam", "shaheeralam.alam@gmail.com", "Shaheer.123");

		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		ResponseEntity<User> expectedResponse = new ResponseEntity<>(user, HttpStatus.OK);
		assertEquals(expectedResponse, userService.getData(id));
	}

	@Test
	public void testCreateUser_Success() {
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

//	@Test
//	public void testVerifyUser_SuccessfulLogin() {
//		// Arrange
//		String email = "shaheeralam.alam@gmail.com";
//		String password = "Shaheer.123";
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		String hashedPassword = encoder.encode(password);
//		String generatedToken = "myJwtToken";
//
//		User user = new User("Shaheer", "Alam", email, hashedPassword);
//
//
//		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//		when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
//		when(jwtService.generateToken(user)).thenReturn(generatedToken);
//
//		// Act
//		ResponseEntity<LoginResponse> response = userService.verifyUser(email, password);
//
//		// Assert
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//		assertNotNull(response.getBody());
//		assertEquals(generatedToken, response.getBody().getToken());
//		assertEquals("User logged in successfully", response.getBody().getMessage());
//		assertTrue(response.getBody().isSuccess());
//	}

	@Test
	public void testVerifyUser_IncorrectPassword() {
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
	public void testGetAllContacts_Success() {

		ContactDetails contact1 = new ContactDetails(
				"John",
				"Doe",
				"Mr.",
				"john.doe@company.com",
				"john.personal@example.com",
				"03121234567", // Home Phone Number starting with 03
				"02131234567", // Work Phone Number starting with 0213
				"03011234567"  // Personal Phone Number starting with 03
		);

		ContactDetails contact2 = new ContactDetails(
				"Jane",
				"Smith",
				"Ms.",
				"jane.smith@company.com",
				"jane.personal@example.com",
				"03221234567", // Home Phone Number starting with 03
				"02131234567", // Work Phone Number starting with 0213
				"03021234567"  // Personal Phone Number starting with 03
		);

		List<ContactDetails> contacts = Arrays.asList(contact1, contact2);
		when(contactDetailsRepository.findAll()).thenReturn(contacts);

		ResponseEntity<ContactListResponse> response = contactDetailsService.getAllContacts();

		assertEquals(contacts, response.getBody().getContactDetailsList());
	}

	@Test
	public void testCreateContact_Success() {
		long id = 999;
		User mockUser = new User("Shaheer", "Alam", "shaheeralam.alam@gmail.com", "hashedPassword");
		ContactDetails mockContactDetails = new ContactDetails(
				"John",
				"Doe",
				"Mr.",
				"john.doe@company.com",
				"john.personal@example.com",
				"03121234567",
				"02131234567",
				"03011234567"
		);
		when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
		when(contactDetailsRepository.save(mockContactDetails)).thenReturn(mockContactDetails);

		ResponseEntity<ContactCreateResponse> response = contactDetailsService.createContact(id, mockContactDetails);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Contact created Successfully", response.getBody().getMessage());
		assertTrue(response.getBody().isSuccess());
	}

}
