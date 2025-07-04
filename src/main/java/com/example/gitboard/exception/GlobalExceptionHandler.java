package com.example.gitboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BoardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleBoardNotFound(BoardNotFoundException e) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", "Not Found");
        error.put("message", e.getMessage());
        error.put("timestamp", LocalDateTime.now());
        return error;
    }
}
