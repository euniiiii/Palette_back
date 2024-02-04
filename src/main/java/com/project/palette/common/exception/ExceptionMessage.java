package com.project.palette.common.exception;

public enum ExceptionMessage {
    EMAIL_ALREADY_IN_USE("이미 사용중인 이메일입니다."),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다.")
    ;

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
         return message;
    }
}
