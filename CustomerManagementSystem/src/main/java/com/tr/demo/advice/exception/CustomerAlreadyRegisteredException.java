package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class CustomerAlreadyRegisteredException extends RuntimeException {

    private final int code;

    public CustomerAlreadyRegisteredException() {
        super("Customer already registered with given username or email");
        this.code = ErrorCodes.CUSTOMER_ALREADY_REGISTERED;
    }
}
