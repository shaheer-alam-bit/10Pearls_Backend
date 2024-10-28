package com.example.contactmanager.DTO;

import java.util.List;

public class SearchResponse
{
    private List<SearchedContacts> results;
    private String message;
    private boolean success;

    public SearchResponse(List<SearchedContacts> results, String message, boolean success) {
        this.results = results;
        this.message = message;
        this.success = success;
    }

    public List<SearchedContacts> getResults() {
        return results;
    }

    public void setResults(List<SearchedContacts> results) {
        this.results = results;
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
