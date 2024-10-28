package com.example.contactmanager.DTO;

import com.example.contactmanager.Model.ContactDetails;

import java.util.List;

public class ContactListResponse
{
    private List<ContactDetails> contactDetailsList;
    private String message;
    private boolean success;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public ContactListResponse(List<ContactDetails> contactDetailsList, String message, boolean success, int currentPage, int totalPages, long totalItems) {
        this.contactDetailsList = contactDetailsList;
        this.message = message;
        this.success = success;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public ContactListResponse(List<ContactDetails> contactDetailsList, String message, boolean success) {
        this.contactDetailsList = contactDetailsList;
        this.message = message;
        this.success = success;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
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
