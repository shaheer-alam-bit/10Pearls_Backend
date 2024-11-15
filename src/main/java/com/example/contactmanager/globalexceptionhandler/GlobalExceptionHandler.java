package com.example.contactmanager.globalexceptionhandler;

import com.example.contactmanager.customexceptions.ContactNotFoundException;
import com.example.contactmanager.customexceptions.InvalidJWTAuthenticationException;
import com.example.contactmanager.customexceptions.KeyErrorException;
import com.example.contactmanager.customexceptions.UserNotFoundException;
import com.example.contactmanager.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KeyErrorException.class)
    public ResponseEntity<ErrorResponse> handleKeyErrorException(KeyErrorException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidJWTAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJWTAuthenticationException(InvalidJWTAuthenticationException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),true), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ContactNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleContactNotFoundException(ContactNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),false), HttpStatus.UNAUTHORIZED);
    }

}
