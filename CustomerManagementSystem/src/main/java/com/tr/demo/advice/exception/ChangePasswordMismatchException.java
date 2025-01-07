package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class ChangePasswordMismatchException extends RuntimeException {

    private final int code;

    public ChangePasswordMismatchException() {
        super("Password mismatch!");
        this.code = ErrorCodes.PASSWORD_MISMATCH;
    }
}
