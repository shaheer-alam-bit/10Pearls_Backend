package com.example.contactmanager.DTO;

import com.example.contactmanager.Model.ContactDetails;

import java.util.List;

public class ContactListResponse
{
    private List<ContactDetails> contactDetailsList;
    private String message;
    private boolean success;

    public ContactListResponse(List<ContactDetails> contactDetailsList, String message, boolean success) {
        this.contactDetailsList = contactDetailsList;
        this.message = message;
        this.success = success;
    }

    public List<ContactDetails> getContactDetailsList() {
        return contactDetailsList;
    }

    public void setContactDetailsList(List<ContactDetails> contactDetailsList) {
        this.contactDetailsList = contactDetailsList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
