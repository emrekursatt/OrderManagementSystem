package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class UserNotEnabledException extends RuntimeException {

    private final int code;

    public UserNotEnabledException() {
        super("User is not enabled!");
        this.code = ErrorCodes.NOT_ENABLED_USER;
    }
}
