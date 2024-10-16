package com.example.contactmanager.Controllers;

import com.example.contactmanager.Model.ContactDetails;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Services.ContactDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class ContactDetailsController
{
    @Autowired
    private ContactDetailsService contactDetailsService;

    @PostMapping("/addContact/{userId}")
    public ResponseEntity<String> addContact(@PathVariable long userId, @RequestBody ContactDetails contactDetails)
    {
        return contactDetailsService.createContact(userId,contactDetails);
    }

    @GetMapping("/getContacts")
    public ResponseEntity<List<ContactDetails>> getContacts()
    {
        return contactDetailsService.getAllContacts();
    }

    @PostMapping("/getContactsById")
    public ResponseEntity<List<ContactDetails>> getContactsByID(@RequestBody User user)
    {
        Long id = user.getId();
        return contactDetailsService.getContactsById(id);
    }

    @PostMapping("/deleteContact/{userId}")
    public ResponseEntity<String> deleteContactByID(@PathVariable long userId, @RequestBody ContactDetails contactDetails)
    {
        long id = contactDetails.getId();
        return contactDetailsService.deleteContact(userId,id);
    }

    @PostMapping("/getContactById")
    public ResponseEntity<ContactDetails> getAContact(@RequestBody ContactDetails contactDetails)
    {
        long id = contactDetails.getId();
        return contactDetailsService.getAContact(id);
    }

    @PostMapping("/findContactByName")
    public ResponseEntity<ContactDetails> findByName(@RequestBody ContactDetails contactDetails)
    {
        String name = contactDetails.getFirstName();
        return contactDetailsService.findByFirstName(name);
    }
}
