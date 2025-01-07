package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class UserIsNotActiveException extends RuntimeException {

    private final int code;

    public UserIsNotActiveException() {
        super("User is not ACTIVE!");
        this.code = ErrorCodes.USER_IS_NOT_ACTIVE;
    }
}
