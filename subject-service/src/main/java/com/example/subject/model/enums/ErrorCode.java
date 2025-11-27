package com.example.subject.model.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    SUBJECT_NAME_EXISTED(1001, HttpStatus.CONFLICT, "Subject name already exists");

    private final int code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
