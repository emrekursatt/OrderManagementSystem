package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class PasswordHasNumberSequenceException extends RuntimeException {

    private final int code;

    public PasswordHasNumberSequenceException() {
        super("Password has same number in sequence more than 2 times!");
        this.code = ErrorCodes.PASSWORD_SEQUENCE;
    }
}
