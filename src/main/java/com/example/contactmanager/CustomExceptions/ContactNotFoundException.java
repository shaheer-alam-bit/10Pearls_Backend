package com.example.contactmanager.CustomExceptions;

public class ContactNotFoundException extends RuntimeException
{
    public ContactNotFoundException(String message)
    {
        super(message);
    }
}
