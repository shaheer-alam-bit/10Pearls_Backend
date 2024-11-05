package com.example.contactmanager.Controllers;

import com.example.contactmanager.DTO.*;
import com.example.contactmanager.Model.ContactDetails;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Services.ContactDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class ContactDetailsController
{

    private final ContactDetailsService contactDetailsService;

    public ContactDetailsController(ContactDetailsService contactDetailsService)
    {
        this.contactDetailsService = contactDetailsService;
    }

    @PostMapping("/addContact/{userId}")
    public ResponseEntity<ContactCreateResponse> addContact(@PathVariable long userId,@Valid @RequestBody ContactDetails contactDetails)
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
    public ResponseEntity<SearchResponse> findByName(@RequestParam long userId, @RequestParam String name)
    {
        return contactDetailsService.findByFirstName(userId,name);
    }

    @PostMapping("/updateContact/{contactId}")
    public ResponseEntity<ContactUpdateResponse> contactUpdate (@PathVariable long contactId, @Valid @RequestBody ContactDetails updateContactRequest)
    {
        return contactDetailsService.updateContact(contactId, updateContactRequest);
    }

    @GetMapping("/export/{userID}")
    public ResponseEntity<byte[]> exportContactsAsVcf(@PathVariable long userID)
    {
        return contactDetailsService.exportContacts(userID);
    }

    @PostMapping("/import")
    public ResponseEntity<ErrorResponse> importContacts(@RequestParam("file") MultipartFile file, @RequestParam("user_id") long userId)
    {
        return contactDetailsService.importContacts(file,userId);
    }
}
