package com.codemountain.benefitapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException e) {
        Map<String, Object> message = new HashMap<>();
        message.put("message", e.getMessage());
        message.put("status", e.getStatus());
        return new ResponseEntity<>(message, e.getStatus());
    }

    @ExceptionHandler(java.lang.Exception.class)
    public ResponseEntity<Object> handleException(java.lang.Exception e) {
        return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
