package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class CustomerIsNotActiveException extends RuntimeException {

    private final int code;

    public CustomerIsNotActiveException() {
        super("User is not ACTIVE!");
        this.code = ErrorCodes.CUSTOMER_IS_NOT_ACTIVE;
    }
}
