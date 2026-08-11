package com.example.buildlog.global.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException(String message) {
        super(HttpStatus.FORBIDDEN, message); // 403 상태 코드와 메시지 함께 설정
    }
}