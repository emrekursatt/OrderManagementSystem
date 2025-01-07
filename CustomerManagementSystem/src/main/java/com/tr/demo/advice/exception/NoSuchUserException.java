package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class NoSuchUserException extends RuntimeException {

    private final int code;

    public NoSuchUserException() {
        super("No Such User!");
        this.code = ErrorCodes.NO_SUCH_USER;
    }
}
