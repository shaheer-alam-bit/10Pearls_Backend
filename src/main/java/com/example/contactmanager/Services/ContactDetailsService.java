package com.example.contactmanager.Services;

import com.example.contactmanager.CustomExceptions.ContactNotFoundException;
import com.example.contactmanager.CustomExceptions.UserNotFoundException;
import com.example.contactmanager.DTO.ContactCreateResponse;
import com.example.contactmanager.DTO.ContactDetailResponse;
import com.example.contactmanager.DTO.ContactListResponse;
import com.example.contactmanager.Model.ContactDetails;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Repositories.ContactDetailsRepository;
import com.example.contactmanager.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactDetailsService
{
    @Autowired
    ContactDetailsRepository contactDetailsRepository;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<ContactListResponse> getAllContacts()
    {
        List<ContactDetails> contacts = contactDetailsRepository.findAll();
        return new ResponseEntity<>(new ContactListResponse(contacts,"Contacts fetched successfully",true), HttpStatus.OK);
    }

    public ResponseEntity<ContactCreateResponse> createContact(Long userId, ContactDetails contactDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        contactDetails.setUser(user);
        contactDetailsRepository.save(contactDetails);
        return new ResponseEntity<>(new ContactCreateResponse("Contact created Successfully",true), HttpStatus.CREATED);
    }

    public ResponseEntity<ContactListResponse> getContactsById(Long user_id)
    {
        User user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<ContactDetails> usersSavedContacts = user.getSavedContacts();
        return new ResponseEntity<>(new ContactListResponse(usersSavedContacts,"Contacts fetched successfully",true), HttpStatus.OK);
    }

    public ResponseEntity<ContactCreateResponse> deleteContact(Long userId, Long contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Find the contact in the user's saved contacts
        ContactDetails contactToDelete = user.getSavedContacts().stream()
                .filter(contact -> contact.getId() == contactId)
                .findFirst()
                .orElse(null);

        if (contactToDelete == null) {
            return new ResponseEntity<>(new ContactCreateResponse("Contact Not Found", false), HttpStatus.NOT_FOUND);
        }

        // Remove the contact from the user's saved contacts list
        user.getSavedContacts().remove(contactToDelete);
        userRepository.save(user); // Save the user entity after removing the contact

        // Now delete the contact from the repository
        contactDetailsRepository.deleteById(contactId);

        return new ResponseEntity<>(new ContactCreateResponse("Contact Deleted Successfully", true), HttpStatus.OK);
    }


    public ResponseEntity<ContactDetailResponse> getAContact(Long contact_id)
    {
        ContactDetails contact = contactDetailsRepository.findById(contact_id).orElseThrow(() -> new ContactNotFoundException("Contact not found"));
        return new ResponseEntity<>(new ContactDetailResponse(contact,"Contact fetched successfully",true), HttpStatus.OK);
    }

    public ResponseEntity<ContactDetailResponse> findByFirstName(String name)
    {
        ContactDetails contact = contactDetailsRepository.findByFirstName(name).orElseThrow(() -> new ContactNotFoundException("Contact not found"));
        return new ResponseEntity<>(new ContactDetailResponse(contact,"Contact Found Successfully",true), HttpStatus.OK);
    }
}
