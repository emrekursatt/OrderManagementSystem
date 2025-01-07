package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class UserAlreadyRegisteredException extends RuntimeException {

    private final int code;

    public UserAlreadyRegisteredException() {
        super("User already registered with given customer id");
        this.code = ErrorCodes.USER_ALREADY_REGISTERED;
    }
}
