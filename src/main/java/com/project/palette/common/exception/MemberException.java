package com.project.palette.common.exception;

import org.springframework.http.HttpStatus;

public class MemberException extends RuntimeException{

    private final HttpStatus status;
    private String message;

    public MemberException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
