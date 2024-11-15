package com.example.contactmanager.services;

import com.example.contactmanager.customexceptions.ContactNotFoundException;
import com.example.contactmanager.customexceptions.UserNotFoundException;
import com.example.contactmanager.dto.*;
import com.example.contactmanager.model.ContactDetails;
import com.example.contactmanager.model.User;
import com.example.contactmanager.repositories.ContactDetailsRepository;
import com.example.contactmanager.repositories.UserRepository;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
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
import org.springframework.web.multipart.MultipartFile;

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
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.DEFAULT_MESSAGE));
        contactDetails.setUser(user);
        contactDetailsRepository.save(contactDetails);
        log.info("New contact created successfully");
        return new ResponseEntity<>(new ContactCreateResponse("Contact created Successfully", true), HttpStatus.CREATED);
    }

    public ResponseEntity<ContactListResponse> getContactsById(Long userId, int page) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(UserNotFoundException.DEFAULT_MESSAGE));

        Pageable pageable = PageRequest.of(page, 5);
        Page<ContactDetails> contactPage = contactDetailsRepository.findByUserId(user.getId(), pageable);

        List<ContactDetails> usersSavedContacts = contactPage.getContent();
        int currentPage = contactPage.getNumber();
        int totalPages = contactPage.getTotalPages();
        long totalItems = contactPage.getTotalElements();

        log.info("A single contact is fetched by the user ID.");
        return new ResponseEntity<>(new ContactListResponse(usersSavedContacts, "Contacts fetched successfully", true, currentPage, totalPages, totalItems), HttpStatus.OK);
    }

    public ResponseEntity<ContactCreateResponse> deleteContact(Long userId, Long contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.DEFAULT_MESSAGE));

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


    public ResponseEntity<ContactDetailResponse> getAContact(Long contactId) {
        ContactDetails contact = contactDetailsRepository.findById(contactId).orElseThrow(() -> new ContactNotFoundException("Contact not found"));
        log.info("Contact fetched successfully by the Contact ID.");
        return new ResponseEntity<>(new ContactDetailResponse(contact, "Contact fetched successfully", true), HttpStatus.OK);
    }

    public ResponseEntity<SearchResponse> findByFirstName(long userID, String name) {
        List<ContactDetails> contact = contactDetailsRepository.findByUserIdAndFirstNameContainingIgnoreCase(userID,name);

        if (contact.isEmpty()) {
            throw new ContactNotFoundException("Contact not found");
        }
        return new ResponseEntity<>(new SearchResponse(contact, "Contacts Found Successfully", true), HttpStatus.OK);
    }

    public ResponseEntity<ContactUpdateResponse> updateContact(Long contactId, ContactDetails request) {
        ContactDetails contactToUpdate = contactDetailsRepository.findById(contactId).orElseThrow(() -> new ContactNotFoundException("Contact Not Found"));

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

    public ResponseEntity<byte[]> exportContacts(long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(UserNotFoundException.DEFAULT_MESSAGE));

            List<ContactDetails> contactsList = user.getSavedContacts();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            for (ContactDetails contact : contactsList) {
                VCard vCard = new VCard();

                vCard.addProperty(new FormattedName(contact.getTitle() + contact.getFirstName() + " " + contact.getLastName()));

                Telephone personalPhone = new Telephone(contact.getPersonalPhoneNumber());
                personalPhone.getTypes().add(TelephoneType.CELL);
                vCard.addProperty(personalPhone);

                Email personalEmail = new Email(contact.getPersonalEmail());
                personalEmail.getTypes().add(EmailType.HOME);
                vCard.addProperty(personalEmail);

                if (contact.getHomePhoneNumber() != null){
                    Telephone homePhone = new Telephone(contact.getHomePhoneNumber());
                    homePhone.getTypes().add(TelephoneType.HOME);
                    vCard.addProperty(homePhone);
                }

                if (contact.getWorkPhoneNumber() != null){
                    Telephone workPhone = new Telephone(contact.getWorkPhoneNumber());
                    workPhone.getTypes().add(TelephoneType.WORK);
                    vCard.addProperty(workPhone);
                }

                if (contact.getWorkEmail() != null) {
                    Email workEmail = new Email(contact.getWorkEmail());
                    workEmail.getTypes().add(EmailType.WORK);
                    vCard.addProperty(workEmail);
                }

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

    public ResponseEntity<ErrorResponse> importContacts(MultipartFile file, long userId)
    {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(UserNotFoundException.DEFAULT_MESSAGE));

            List<VCard> vCardsList = Ezvcard.parse(file.getInputStream()).all();

            for (VCard vcard : vCardsList)
            {
                parseContactDetails(vcard,user);
            }
            userRepository.save(user);
            return new ResponseEntity<>(new ErrorResponse("Contacts imported successfully",true), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error importing contacts: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public void parseContactDetails(VCard vcard , User user)
    {
        ContactDetails contact = new ContactDetails();

        FormattedName fn = vcard.getFormattedName();
        if (fn != null)
        {
            String[] arr = fn.getValue().split("[.\\s]+");
            contact.setTitle(arr[0]+".");
            contact.setFirstName(arr[1]);
            contact.setLastName(arr[2]);
        }

        List<Telephone> telephoneList = vcard.getTelephoneNumbers();
        for (Telephone telephone : telephoneList) {
            String type = telephone.getParameter("TYPE");
            String number = telephone.getText();

            if (type.equalsIgnoreCase("cell")){
                contact.setPersonalPhoneNumber(number);
            } else if (type.equalsIgnoreCase("home")){
                contact.setHomePhoneNumber(number);
            } else if (type.equalsIgnoreCase("work")){
                contact.setWorkPhoneNumber(number);
            }
        }

        List<Email> emailList = vcard.getEmails();
        for (Email email : emailList)
        {
            String type = email.getParameter("TYPE");
            String emailAddress = email.getValue();

            if (type.equalsIgnoreCase("home")){
                contact.setPersonalEmail(emailAddress);
            } else if (type.equalsIgnoreCase("work")){
                contact.setWorkEmail(emailAddress);
            }
        }
        contact.setUser(user);
        user.getSavedContacts().add(contact);
    }
}