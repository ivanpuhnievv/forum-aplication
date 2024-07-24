package com.example.forumapplication.advice;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.exceptions.UnauthorizedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleException(MethodArgumentNotValidException exception) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {

            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }

    @ExceptionHandler(AuthorizationException.class)
    public Map<String, String> handleException(AuthorizationException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message ", exception.getMessage());
        return errorMap;
    }

    @ExceptionHandler(EntityDuplicateException.class)
    public Map<String, String> handleException(EntityDuplicateException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message ", exception.getMessage());
        return errorMap;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public Map<String, String> handleException(EntityNotFoundException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message ", exception.getMessage());
        return errorMap;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Map<String, String> handleException(UnauthorizedException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message ", exception.getMessage());
        return errorMap;
    }

}
