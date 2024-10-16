package com.example.contactmanager.GlobalExceptionHandler;

import com.example.contactmanager.CustomExceptions.InvalidJWTAuthenticationException;
import com.example.contactmanager.CustomExceptions.UserNotFoundException;
import com.example.contactmanager.DTO.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),true), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidJWTAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJWTAuthenticationException(InvalidJWTAuthenticationException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),true), HttpStatus.UNAUTHORIZED);
    }

}
