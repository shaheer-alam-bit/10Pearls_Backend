package com.example.contactmanager.Controllers;

import com.example.contactmanager.DTO.ContactCreateResponse;
import com.example.contactmanager.DTO.ContactDetailResponse;
import com.example.contactmanager.DTO.ContactListResponse;
import com.example.contactmanager.Model.ContactDetails;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Services.ContactDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class ContactDetailsController
{
    @Autowired
    private ContactDetailsService contactDetailsService;

    @PostMapping("/addContact/{userId}")
    public ResponseEntity<ContactCreateResponse> addContact(@PathVariable long userId, @RequestBody ContactDetails contactDetails)
    {
        return contactDetailsService.createContact(userId,contactDetails);
    }

    @GetMapping("/getContacts")
    public ResponseEntity<ContactListResponse> getContacts()
    {
        return contactDetailsService.getAllContacts();
    }

    @PostMapping("/getContactsById")
    public ResponseEntity<ContactListResponse> getContactsByID(@RequestBody User user)
    {
        Long id = user.getId();
        return contactDetailsService.getContactsById(id);
    }

    @PostMapping("/deleteContact/{userId}")
    public ResponseEntity<ContactCreateResponse> deleteContactByID(@PathVariable long userId, @RequestBody ContactDetails contactDetails)
    {
        long id = contactDetails.getId();
        return contactDetailsService.deleteContact(userId,id);
    }

    @PostMapping("/getContactById")
    public ResponseEntity<ContactDetailResponse> getAContact(@RequestBody ContactDetails contactDetails)
    {
        long id = contactDetails.getId();
        return contactDetailsService.getAContact(id);
    }

    @PostMapping("/findContactByName")
    public ResponseEntity<ContactDetailResponse> findByName(@RequestBody ContactDetails contactDetails)
    {
        String name = contactDetails.getFirstName();
        return contactDetailsService.findByFirstName(name);
    }
}
