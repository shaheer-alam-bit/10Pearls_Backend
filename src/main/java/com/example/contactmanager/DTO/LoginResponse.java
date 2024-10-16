package com.example.contactmanager.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse
{
    private String token;
    private String message;

    // For successful login
    public LoginResponse (String token, String message) {
        this.token = token;
        this.message = message;
    }

    //For unsuccessful login
    public LoginResponse (String message){
        this.message = message;
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
}
