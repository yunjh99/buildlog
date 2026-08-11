package com.example.buildlog.global.exception;

import com.example.buildlog.global.common.FailResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<FailResponse> handleBadCredentials(BadCredentialsException exception) {
        FailResponse response = FailResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<FailResponse> handleResponseStatusException(
            ResponseStatusException exception
    ) {
        int status = exception.getStatusCode().value();
        String message = exception.getReason() != null
                ? exception.getReason()
                : "요청을 처리할 수 없습니다.";

        FailResponse response = FailResponse.builder()
                .status(status)
                .message(message)
                .build();

        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }
}
