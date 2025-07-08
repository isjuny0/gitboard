package com.example.gitboard.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("해당 요청에 대한 권한이 없습니다.");
    }
}
