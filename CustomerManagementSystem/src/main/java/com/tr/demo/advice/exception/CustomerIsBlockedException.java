package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class CustomerIsBlockedException extends RuntimeException {

    private final int code;

    public CustomerIsBlockedException() {
        super("User is blocked!");
        this.code = ErrorCodes.CUSTOMER_BLOCKED;
    }
}
