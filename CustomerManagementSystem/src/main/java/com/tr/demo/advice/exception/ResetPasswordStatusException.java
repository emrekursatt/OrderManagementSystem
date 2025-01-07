package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class ResetPasswordStatusException extends RuntimeException {

    private final int code;

    public ResetPasswordStatusException(){
        super("User status mismatched to change password.");
        this.code = ErrorCodes.RESET_PASSWORD_REQUEST_STATUS_ERROR;
    }
}
