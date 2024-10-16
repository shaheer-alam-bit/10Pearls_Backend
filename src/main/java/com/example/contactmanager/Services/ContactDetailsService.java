package com.example.contactmanager.Services;

import com.example.contactmanager.CustomExceptions.UserNotFoundException;
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

    public ResponseEntity<List<ContactDetails>> getAllContacts()
    {
        List<ContactDetails> contacts = contactDetailsRepository.findAll();
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    public ResponseEntity<String> createContact(Long userId, ContactDetails contactDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        contactDetails.setUser(user);
        contactDetailsRepository.save(contactDetails);
        return new ResponseEntity<>("Contact created successfully", HttpStatus.OK);
    }

    public ResponseEntity<List<ContactDetails>> getContactsById(Long user_id)
    {
        User user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<ContactDetails> usersSavedContacts = user.getSavedContacts();
        return new ResponseEntity<>(usersSavedContacts, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteContact(Long userId, Long contact_id)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!user.getSavedContacts().contains(contact_id))
        {
            return new ResponseEntity<>("Contact not found", HttpStatus.NOT_FOUND);
        }
        contactDetailsRepository.deleteById(contact_id);
        return new ResponseEntity<>("Contact deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<ContactDetails> getAContact(Long contact_id)
    {
        ContactDetails contact = contactDetailsRepository.findById(contact_id).orElseThrow(() -> new RuntimeException("Contact not found"));
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    public ResponseEntity<ContactDetails> findByFirstName(String name)
    {
        ContactDetails contact = contactDetailsRepository.findByFirstName(name).orElseThrow(() -> new RuntimeException("Contact not found"));
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }
}
