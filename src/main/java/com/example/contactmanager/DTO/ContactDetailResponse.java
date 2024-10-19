package com.example.contactmanager.DTO;

import com.example.contactmanager.Model.ContactDetails;

public class ContactDetailResponse
{
    private ContactDetails contactDetails;
    private String message;
    private boolean success;

    public ContactDetailResponse(ContactDetails contactDetails, String message, boolean success) {
        this.contactDetails = contactDetails;
        this.message = message;
        this.success = success;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
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
