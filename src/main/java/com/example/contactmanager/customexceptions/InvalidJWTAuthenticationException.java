package com.example.contactmanager.customexceptions;

public class InvalidJWTAuthenticationException extends RuntimeException {

    public InvalidJWTAuthenticationException(String message)
    {
        super(message);
    }
}
