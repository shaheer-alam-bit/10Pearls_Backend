package com.example.contactmanager.dto;

public class SearchedContacts
{
    private String title;
    private String firstName;
    private String lastName;
    private String personalPhoneNumber;
    private String personalEmail;

    public SearchedContacts(String title, String firstName, String lastName, String personalPhoneNumber, String personalEmail) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalPhoneNumber = personalPhoneNumber;
        this.personalEmail = personalEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPersonalPhoneNumber() {
        return personalPhoneNumber;
    }

    public void setPersonalPhoneNumber(String personalPhoneNumber) {
        this.personalPhoneNumber = personalPhoneNumber;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }
}
