package com.example.contactmanager.ContactTests;

import com.example.contactmanager.CustomExceptions.ContactNotFoundException;
import com.example.contactmanager.DTO.ContactCreateResponse;
import com.example.contactmanager.DTO.ContactDetailResponse;
import com.example.contactmanager.DTO.ContactListResponse;
import com.example.contactmanager.DTO.ContactUpdateResponse;
import com.example.contactmanager.Model.ContactDetails;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Repositories.ContactDetailsRepository;
import com.example.contactmanager.Repositories.UserRepository;
import com.example.contactmanager.Services.ContactDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest
{

    @InjectMocks
    ContactDetailsService contactDetailsService;

    @Mock
    ContactDetailsRepository contactDetailsRepository;

    @Mock
    UserRepository userRepository;

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

    @Test
    public void testGetAllContacts_Success() {


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
        assertEquals("Contact created Successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testDeleteContact_Success() {

        Long userId = 1L;
        Long contactId = 2L;

        User mockUser = new User();
        mockUser.setId(userId);

        ContactDetails mockContact = new ContactDetails(
                "John",
                "Doe",
                "Mr.",
                "john.doe@company.com",
                "john.personal@example.com",
                "03121234567",
                "02131234567",
                "03011234567"
        );
        mockContact.setId(contactId);

        List<ContactDetails> savedContacts = new ArrayList<>();
        savedContacts.add(mockContact);
        mockUser.setSavedContacts(savedContacts);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        ResponseEntity<ContactCreateResponse> response = contactDetailsService.deleteContact(userId, contactId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Contact Deleted Successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    public void testDeleteContact_ContactNotFound() {
        Long userId = 1L;
        Long contactId = 99L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setSavedContacts(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        ResponseEntity<ContactCreateResponse> response = contactDetailsService.deleteContact(userId, contactId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Contact Not Found", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    public void testUpdateContact_Success() {

        Long contactId = 1L;
        ContactDetails existingContact = new ContactDetails(
                "John",
                "Doe",
                "Mr.",
                "john.doe@company.com",
                "john.personal@example.com",
                "03121234567",
                "02131234567",
                "03011234567"
        );
        existingContact.setId(contactId);

        ContactDetails updatedContactDetails = new ContactDetails(
                "Johnny",
                "Doe",
                "Mr.",
                "johnny.doe@company.com",
                "johnny.personal@example.com",
                "03121234568",
                "02131234568",
                "03011234568"
        );

        when(contactDetailsRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactDetailsRepository.save(existingContact)).thenReturn(existingContact);

        ResponseEntity<ContactUpdateResponse> response = contactDetailsService.updateContact(contactId, updatedContactDetails);

        assertEquals("Contact Updated Successfully", response.getBody().getMessage());
        assertEquals(updatedContactDetails.getFirstName(), existingContact.getFirstName());
        assertEquals(updatedContactDetails.getLastName(), existingContact.getLastName());
        assertEquals(updatedContactDetails.getWorkEmail(), existingContact.getWorkEmail());
        assertEquals(updatedContactDetails.getPersonalEmail(), existingContact.getPersonalEmail());
        assertEquals(updatedContactDetails.getTitle(), existingContact.getTitle());
        assertEquals(updatedContactDetails.getHomePhoneNumber(), existingContact.getHomePhoneNumber());
        assertEquals(updatedContactDetails.getPersonalPhoneNumber(), existingContact.getPersonalPhoneNumber());
        assertEquals(updatedContactDetails.getWorkPhoneNumber(), existingContact.getWorkPhoneNumber());
    }

    @Test
    public void testGetAContact_Success() {

        Long contactId = 1L;
        ContactDetails contact = new ContactDetails(
                "John",
                "Doe",
                "Mr.",
                "john.doe@company.com",
                "john.personal@example.com",
                "03121234567",
                "02131234567",
                "03011234567"
        );
        contact.setId(contactId);

        when(contactDetailsRepository.findById(contactId)).thenReturn(Optional.of(contact));

        ResponseEntity<ContactDetailResponse> response = contactDetailsService.getAContact(contactId);

        assertEquals("Contact fetched successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        assertEquals(contact, response.getBody().getContactDetails());
    }

    @Test
    public void testGetAContact_ContactNotFound() {
        Long contactId = 1L;

        when(contactDetailsRepository.findById(contactId)).thenReturn(Optional.empty());

        ContactNotFoundException exception = assertThrows(ContactNotFoundException.class,
                () -> contactDetailsService.getAContact(contactId));
        assertEquals("Contact not found", exception.getMessage());
    }



}
