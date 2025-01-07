package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class InvalidRefreshTokenException extends RuntimeException {

    private final int code;

    public InvalidRefreshTokenException() {
        super("Invalid refresh token, please sign in");
        this.code = ErrorCodes.INVALID_REFRESH_TOKEN;
    }
}
