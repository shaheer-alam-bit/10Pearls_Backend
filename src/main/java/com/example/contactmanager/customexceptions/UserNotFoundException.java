package com.example.contactmanager.customexceptions;

public class UserNotFoundException extends RuntimeException
{
    public static final String DEFAULT_MESSAGE = "User Not Found";

    public UserNotFoundException(String message)
    {
        super(message);
    }
}
