package com.example.subject.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    INVALID_REQUEST(400, HttpStatus.BAD_REQUEST, "Invalid request"),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "Access denied"),
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "Resource not found"),
    // =========== Subject Errors ============
    SUBJECT_NAME_EXISTED(1001, HttpStatus.CONFLICT, "Subject name already exists"),
    SUBJECT_CODE_EXISTED(1003, HttpStatus.CONFLICT, "Subject code already exists"),
    SUBJECT_NOT_FOUND(1002, HttpStatus.NOT_FOUND, "Subject not found"),
    // =========== Topic Errors ============
    TOPIC_NOT_FOUND(2001, HttpStatus.NOT_FOUND, "Topic not found"),
    TOPIC_ALREADY_EXISTS(2002, HttpStatus.CONFLICT, "Topic already exists"),
    // =========== Instructor Assignment Errors ============
    INSTRUCTOR_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "Instructor not found"),
    INSTRUCTOR_NOT_VALID(3002, HttpStatus.BAD_REQUEST, "User is not an instructor"),
    INSTRUCTOR_ALREADY_ASSIGNED(3003, HttpStatus.CONFLICT, "Instructor already assigned to this subject"),
    INSTRUCTOR_NOT_ASSIGNED(3004, HttpStatus.NOT_FOUND, "Instructor not assigned to this subject"),
    USER_SERVICE_UNAVAILABLE(3005, HttpStatus.SERVICE_UNAVAILABLE, "User service is temporarily unavailable");

    private final Integer code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(Integer code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
