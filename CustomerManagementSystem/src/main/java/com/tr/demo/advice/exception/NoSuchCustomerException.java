package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class NoSuchCustomerException extends RuntimeException {

    private final int code;

    public NoSuchCustomerException() {
        super("No Such User!");
        this.code = ErrorCodes.NO_SUCH_CUSTOMER;
    }
}
