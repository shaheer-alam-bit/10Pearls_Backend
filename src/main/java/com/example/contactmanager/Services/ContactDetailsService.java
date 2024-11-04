package com.example.contactmanager.Services;

import com.example.contactmanager.CustomExceptions.ContactNotFoundException;
import com.example.contactmanager.CustomExceptions.UserNotFoundException;
import com.example.contactmanager.DTO.*;
import com.example.contactmanager.Model.ContactDetails;
import com.example.contactmanager.Model.User;
import com.example.contactmanager.Repositories.ContactDetailsRepository;
import com.example.contactmanager.Repositories.UserRepository;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.Email;
import ezvcard.property.FormattedName;
import ezvcard.property.Telephone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
@Service
public class ContactDetailsService {

    private final ContactDetailsRepository contactDetailsRepository;
    private final UserRepository userRepository;

    public ContactDetailsService(ContactDetailsRepository contactDetailsRepository, UserRepository userRepository) {
        this.contactDetailsRepository = contactDetailsRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<ContactListResponse> getAllContacts() {
        List<ContactDetails> contacts = contactDetailsRepository.findAll();
        log.info("All contacts fetched successfully");
        return new ResponseEntity<>(new ContactListResponse(contacts, "Contacts fetched successfully", true), HttpStatus.OK);
    }

    public ResponseEntity<ContactCreateResponse> createContact(Long userId, ContactDetails contactDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        contactDetails.setUser(user);
        contactDetailsRepository.save(contactDetails);
        log.info("New contact created successfully");
        return new ResponseEntity<>(new ContactCreateResponse("Contact created Successfully", true), HttpStatus.CREATED);
    }

    public ResponseEntity<ContactListResponse> getContactsById(Long user_id, int page) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, 5);
        Page<ContactDetails> contactPage = contactDetailsRepository.findByUserId(user_id, pageable);

        List<ContactDetails> usersSavedContacts = contactPage.getContent();
        int currentPage = contactPage.getNumber();
        int totalPages = contactPage.getTotalPages();
        long totalItems = contactPage.getTotalElements();

        log.info("A single contact is fetched by the user ID.");
        return new ResponseEntity<>(new ContactListResponse(usersSavedContacts, "Contacts fetched successfully", true, currentPage, totalPages, totalItems), HttpStatus.OK);
    }

    public ResponseEntity<ContactCreateResponse> deleteContact(Long userId, Long contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ContactDetails contactToDelete = user.getSavedContacts().stream()
                .filter(contact -> contact.getId() == contactId)
                .findFirst()
                .orElse(null);

        if (contactToDelete == null) {
            log.error("Contact to be deleted not found");
            return new ResponseEntity<>(new ContactCreateResponse("Contact Not Found", false), HttpStatus.NOT_FOUND);
        }

        user.getSavedContacts().remove(contactToDelete);
        userRepository.save(user);

        contactDetailsRepository.deleteById(contactId);
        log.info("Contact deleted successfully by the user ID.");
        return new ResponseEntity<>(new ContactCreateResponse("Contact Deleted Successfully", true), HttpStatus.OK);
    }


    public ResponseEntity<ContactDetailResponse> getAContact(Long contact_id) {
        ContactDetails contact = contactDetailsRepository.findById(contact_id).orElseThrow(() -> new ContactNotFoundException("Contact not found"));
        log.info("Contact fetched successfully by the Contact ID.");
        return new ResponseEntity<>(new ContactDetailResponse(contact, "Contact fetched successfully", true), HttpStatus.OK);
    }

    public ResponseEntity<SearchResponse> findByFirstName(long userID, String name) {
        List<ContactDetails> contact = contactDetailsRepository.findByUserIdAndFirstNameContainingIgnoreCase(userID,name);

        if (contact.isEmpty()) {
            throw new ContactNotFoundException("Contact not found");
        }
//        List<ContactDetails> searchedList = contact.stream()
//                .map(cont -> new ContactDetails(cont.getTitle(), cont.getFirstName(), cont.getLastName(), cont.getPersonalPhoneNumber(), cont.getPersonalEmail()))
//                .toList();

        return new ResponseEntity<>(new SearchResponse(contact, "Contacts Found Successfully", true), HttpStatus.OK);
    }

    public ResponseEntity<ContactUpdateResponse> updateContact(Long contact_id, ContactDetails request) {
        ContactDetails contactToUpdate = contactDetailsRepository.findById(contact_id).orElseThrow(() -> new ContactNotFoundException("Contact Not Found"));

        contactToUpdate.setFirstName(request.getFirstName());
        contactToUpdate.setLastName(request.getLastName());
        contactToUpdate.setPersonalEmail(request.getPersonalEmail());
        contactToUpdate.setTitle(request.getTitle());
        contactToUpdate.setWorkEmail(request.getWorkEmail());
        contactToUpdate.setHomePhoneNumber(request.getHomePhoneNumber());
        contactToUpdate.setPersonalPhoneNumber(request.getPersonalPhoneNumber());
        contactToUpdate.setWorkPhoneNumber(request.getWorkPhoneNumber());

        contactDetailsRepository.save(contactToUpdate);
        log.info("Contact details updated successfully by the contact ID.");
        return new ResponseEntity<>(new ContactUpdateResponse("Contact Updated Successfully", true), HttpStatus.OK);
    }

    public ResponseEntity<byte[]> exportContacts(long user_id) {
        try {
            User user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User not found"));

            List<ContactDetails> contactsList = user.getSavedContacts();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            for (ContactDetails contact : contactsList) {
                VCard vCard = new VCard();

                vCard.addProperty(new FormattedName(contact.getFirstName() + " " + contact.getLastName()));
                vCard.addProperty(new Telephone(contact.getPersonalPhoneNumber()));
                vCard.addProperty(new Email(contact.getPersonalEmail()));

                Ezvcard.write(vCard).go(outputStream);
            }

            byte[] outputData = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.vcf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(outputData.length)
                    .contentType(MediaType.parseMediaType("text/vcard"))
                    .body(outputData);
        } catch (Exception e)
        {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}