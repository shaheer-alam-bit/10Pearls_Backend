package com.example.contactmanager.ContactTests;

import com.example.contactmanager.controllers.ContactDetailsController;
import com.example.contactmanager.dto.*;
import com.example.contactmanager.model.ContactDetails;
import com.example.contactmanager.services.ContactDetailsService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContactDetailsControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ContactDetailsService contactDetailsService;

    @InjectMocks
    private ContactDetailsController contactDetailsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(contactDetailsController).build();
    }

    @Test
    void testGetContacts() throws Exception {

        ContactDetails contact1 = new ContactDetails(
                "John",
                "Doe",
                "Mr.",
                "john.doe@company.com",
                "john.personal@example.com",
                "03121234567",
                "02131234567",
                "03011234567"
        );

        ContactDetails contact2 = new ContactDetails(
                "Jane",
                "Smith",
                "Ms.",
                "jane.smith@company.com",
                "jane.personal@example.com",
                "03221234567",
                "02131234567",
                "03021234567"
        );

        List<ContactDetails> contacts = Arrays.asList(contact1, contact2);
        ContactListResponse response = new ContactListResponse(contacts, "Contacts fetched successfully", true);

        when(contactDetailsService.getAllContacts()).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        mockMvc.perform(get("/getContacts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAddContactStatus() throws Exception {

        long userId = 1L;
        ContactDetails contactDetails = new ContactDetails(
                "John", "Doe", "Mr.", "john.doe@company.com",
                "john.personal@example.com", "03121234567",
                "02131234567", "03011234567"
        );
        ContactCreateResponse response = new ContactCreateResponse("Contact created Successfully", true);
        ObjectMapper objectMapper = new ObjectMapper();

        when(contactDetailsService.createContact(userId, contactDetails))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.CREATED));

        // Act & Assert: Perform POST request and verify status is 200 Created
        mockMvc.perform(post("/addContact/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(contactDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteContactStatus() throws Exception {
        // Arrange: Mock the service response for the status
        long userId = 1L;
        long contactId = 101L;
        ContactDetails contactDetails = new ContactDetails(
                "John", "Doe", "Mr.", "john.doe@company.com",
                "john.personal@example.com", "03121234567",
                "02131234567", "03011234567"
        );
        contactDetails.setId(contactId);

        ContactCreateResponse response = new ContactCreateResponse("Contact Deleted Successfully", true);
        ObjectMapper objectMapper = new ObjectMapper();

        when(contactDetailsService.deleteContact(userId, contactId))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        // Act & Assert: Perform POST request and verify status is 200 OK
        mockMvc.perform(post("/deleteContact/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(contactDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetContactByIdStatus() throws Exception {
        // Arrange: Mock the service response for the status
        long contactId = 101L;
        ContactDetails contactDetails = new ContactDetails(
                "John", "Doe", "Mr.", "john.doe@company.com",
                "john.personal@example.com", "03121234567",
                "02131234567", "03011234567"
        );
        contactDetails.setId(contactId);

        ContactDetailResponse response = new ContactDetailResponse(contactDetails, "Contact fetched successfully", true);
        ObjectMapper objectMapper = new ObjectMapper();

        when(contactDetailsService.getAContact(contactId))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        // Act & Assert: Perform POST request and verify status is 200 OK
        mockMvc.perform(post("/getContactById")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(contactDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchStatus() throws Exception {
        // Arrange: Mock the service response for the status
        long userId = 1L;
        String name = "John";

        ContactDetails contact1 = new ContactDetails("Mr.", "John", "Doe", "john.doe@company.com", "john.personal@example.com", "03121234567", "02131234567", "03011234567");
        ContactDetails contact2 = new ContactDetails("Mr.", "John", "Smith", "john.smith@company.com", "john.smith@example.com", "03121234568", "02131234568", "03011234568");

        List<ContactDetails> contacts = Arrays.asList(contact1, contact2);

        SearchResponse response = new SearchResponse(contacts, "Contacts Found Successfully", true);

        when(contactDetailsService.findByFirstName(userId, name))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        // Act & Assert: Perform GET request and verify status is 200 OK
        mockMvc.perform(get("/search")
                        .param("userId", String.valueOf(userId))
                        .param("name", name))
                .andExpect(status().isOk());
    }

    @Test
    void testContactUpdateStatus() throws Exception {
        // Arrange: Create a dummy update request and mock the service response for the status
        long contactId = 1L;
        ContactDetails updateContactRequest = new ContactDetails(
                "Mr.", "John", "Doe", "john.doe@company.com",
                "john.personal@example.com", "03121234567",
                "02131234567", "03011234567"
        );

        ContactUpdateResponse response = new ContactUpdateResponse("Contact Updated Successfully", true);

        when(contactDetailsService.updateContact(contactId, updateContactRequest))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        ObjectMapper objectMapper = new ObjectMapper();

        // Act & Assert: Perform POST request and verify status is 200 OK
        mockMvc.perform(post("/updateContact/{contactId}", contactId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateContactRequest)))
                .andExpect(status().isOk());
    }


}
