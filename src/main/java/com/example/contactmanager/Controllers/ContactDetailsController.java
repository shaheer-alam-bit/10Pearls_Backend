package com.example.contactmanager.Controllers;

import com.example.contactmanager.DTO.*;
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
    public ResponseEntity<ContactListResponse> getContactsByID(@RequestBody User user , @RequestParam(defaultValue = "0") int page)
    {
        Long id = user.getId();
        return contactDetailsService.getContactsById(id,page);
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

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> findByName(@RequestParam String name)
    {
        return contactDetailsService.findByFirstName(name);
    }

    @PostMapping("/updateContact/{contactId}")
    public ResponseEntity<ContactUpdateResponse> contactUpdate (@PathVariable long contactId, @RequestBody UpdateContactRequest updateContactRequest)
    {
        return contactDetailsService.updateContact(contactId, updateContactRequest);
    }
}
