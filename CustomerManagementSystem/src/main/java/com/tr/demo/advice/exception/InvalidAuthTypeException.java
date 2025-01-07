package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class InvalidAuthTypeException extends RuntimeException {

    private final int code;

    public InvalidAuthTypeException() {
        super("Unexpected id/identityNo provided!");
        this.code = ErrorCodes.INVALID_AUTH_TYPE;
    }
}
