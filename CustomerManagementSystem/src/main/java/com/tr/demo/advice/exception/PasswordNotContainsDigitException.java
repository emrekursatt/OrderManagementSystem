package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class PasswordNotContainsDigitException extends RuntimeException {

    private final int code;

    public PasswordNotContainsDigitException() {
        super("Password must contain both number and letter!");
        this.code = ErrorCodes.PASSWORD_NOT_CONTAIN_DIGIT;
    }
}
