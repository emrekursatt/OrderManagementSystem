package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class UserIsBlockedException extends RuntimeException {

    private final int code;

    public UserIsBlockedException() {
        super("User is blocked!");
        this.code = ErrorCodes.USER_BLOCKED;
    }
}
