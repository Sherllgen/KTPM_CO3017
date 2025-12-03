package com.example.user.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    INVALID_REQUEST(400, HttpStatus.BAD_REQUEST, "Invalid request"),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "Access denied"),
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "Resource not found"),
    
    // ====== USER ======
    USER_NOT_FOUND(1001, HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(1002, HttpStatus.CONFLICT, "User already exists"),
    // ============ PASSWORD ===========
    INVALID_OTP_CODE(2001, HttpStatus.BAD_REQUEST, "Invalid OTP code"),
    OLD_PASSWORD_INCORRECT(2003, HttpStatus.BAD_REQUEST, "Old password is incorrect"),
    NEW_PASSWORD_MISMATCH(2004, HttpStatus.BAD_REQUEST, "New password and confirm password do not match"),
    NEW_PASSWORD_SAME_AS_OLD(2005, HttpStatus.BAD_REQUEST, "New password must be different from old password"),
    EMAIL_NOT_FOUND(2006, HttpStatus.NOT_FOUND, "Email not found"),
    ACCOUNT_NOT_ACTIVE(2007, HttpStatus.BAD_REQUEST, "Account is not active"),
    ;

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
