package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class PasswordLengthException extends RuntimeException {

    private final int code;

    public PasswordLengthException() {
        super("Password length can be between 8 and 20");
        this.code = ErrorCodes.PASSWORD_LENGTH;
    }
}
