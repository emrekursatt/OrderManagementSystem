package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class PasswordChangeRequiredException extends RuntimeException {

    private final int code;

    public PasswordChangeRequiredException() {
        super("Password must be changed!");
        this.code = ErrorCodes.PASSWORD_CHANGE_REQUIRED;
    }
}
