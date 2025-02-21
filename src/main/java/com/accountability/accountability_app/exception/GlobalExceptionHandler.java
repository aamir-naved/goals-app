package com.accountability.accountability_app.exception;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.core.io.ClassPathResource;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);  // 409 Conflict
    }

    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<String> handleGoalNotFoundException(GoalNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

//    @ExceptionHandler(UnauthorizedRequestException.class)
//    public ResponseEntity<String> handleUnauthorizedRequestException(UnauthorizedRequestException ex) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
//
//    }

    @ExceptionHandler(UnauthorizedRequestException.class)
    public ResponseEntity<Resource> handleUnauthorizedRequestException(UnauthorizedRequestException ex) throws IOException {
        // Log the exception or handle it as needed
        // Log the message if you want to record that an exception occurred.

        // Load the image file from the resources folder (as an example)
        Resource imageResource = new ClassPathResource("static/img.png");

        // Return the image as a response
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.IMAGE_PNG) // Set the appropriate media type for the image
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"img.png\"")
                .body(imageResource);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}
