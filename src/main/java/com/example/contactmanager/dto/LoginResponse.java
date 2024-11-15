package com.example.contactmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse
{
    private String token;
    private String message;
    private boolean success;

    // For successful login
    public LoginResponse (String token, String message, boolean success) {
        this.token = token;
        this.message = message;
        this.success = success;
    }

    //For unsuccessful login
    public LoginResponse (String message,boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
