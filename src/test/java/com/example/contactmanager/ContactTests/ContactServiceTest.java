package com.example.contactmanager.ContactTests;

import com.example.contactmanager.customexceptions.ContactNotFoundException;
import com.example.contactmanager.customexceptions.UserNotFoundException;
import com.example.contactmanager.dto.*;
import com.example.contactmanager.model.ContactDetails;
import com.example.contactmanager.model.User;
import com.example.contactmanager.repositories.ContactDetailsRepository;
import com.example.contactmanager.repositories.UserRepository;
import com.example.contactmanager.services.ContactDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest
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
    void testGetAllContacts_Success() {


        List<ContactDetails> contacts = Arrays.asList(contact1, contact2);
        when(contactDetailsRepository.findAll()).thenReturn(contacts);

        ResponseEntity<ContactListResponse> response = contactDetailsService.getAllContacts();

        assertEquals(contacts, response.getBody().getContactDetailsList());
    }

    @Test
    void testCreateContact_Success() {
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
    void testDeleteContact_Success() {

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
    void testDeleteContact_ContactNotFound() {
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
    void testUpdateContact_Success() {

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
    void testGetAContact_Success() {

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
    void testGetAContact_ContactNotFound() {
        Long contactId = 1L;

        when(contactDetailsRepository.findById(contactId)).thenReturn(Optional.empty());

        ContactNotFoundException exception = assertThrows(ContactNotFoundException.class,
                () -> contactDetailsService.getAContact(contactId));
        assertEquals("Contact not found", exception.getMessage());
    }

    @Test
    void testImportContacts_Success() throws Exception {
        // Mock data
        long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setSavedContacts(new ArrayList<>());

        String vCardContent = "BEGIN:VCARD\n" +
                "VERSION:4.0\n" +
                "FN:Mr.John Doe\n" +
                "TEL;TYPE=cell:1234567890\n" +
                "TEL;TYPE=work:1234567891\n" +
                "TEL;TYPE=home:1234567892\n" +
                "EMAIL;TYPE=home:john.doe@example.com\n" +
                "EMAIL;TYPE=work:johnwork.doe@example.com\n" +
                "END:VCARD";

        MockMultipartFile file = new MockMultipartFile("file", "contacts.vcf", "text/vcard",
                new ByteArrayInputStream(vCardContent.getBytes()));

        // Mock behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Call service method
        ResponseEntity<ErrorResponse> response = contactDetailsService.importContacts(file, userId);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Contacts imported successfully", response.getBody().getMessage());
        assertEquals(1, mockUser.getSavedContacts().size());

        // Verify contact details
        ContactDetails contact = mockUser.getSavedContacts().get(0);
        assertEquals("Mr", contact.getTitle());
        assertEquals("John", contact.getFirstName());
        assertEquals("Doe", contact.getLastName());
        assertEquals("1234567890", contact.getPersonalPhoneNumber());
        assertEquals("john.doe@example.com", contact.getPersonalEmail());

    }


}
