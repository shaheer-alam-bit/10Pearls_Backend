package com.example.contactmanager.CustomExceptions;

public class InvalidJWTAuthenticationException extends RuntimeException {

    public InvalidJWTAuthenticationException(String message)
    {
        super(message);
    }
}
