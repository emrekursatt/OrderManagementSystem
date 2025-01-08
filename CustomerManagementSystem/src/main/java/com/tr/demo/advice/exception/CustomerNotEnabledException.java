package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class CustomerNotEnabledException extends RuntimeException {

    private final int code;

    public CustomerNotEnabledException() {
        super("User is not enabled!");
        this.code = ErrorCodes.NOT_ENABLED_CUSTOMER;
    }
}
