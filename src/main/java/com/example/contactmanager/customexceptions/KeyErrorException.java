package com.example.contactmanager.customexceptions;

public class KeyErrorException extends RuntimeException
{
    public KeyErrorException(String message)
    {
        super(message);
    }
}
