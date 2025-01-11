package com.tr.demo.advice.exception;

import com.tr.demo.advice.constans.ErrorCodes;
import lombok.Getter;

@Getter
public class ProductAlreadyExistException extends RuntimeException {
    private final int code;

    public ProductAlreadyExistException() {
        super("Product already exists");
        this.code = ErrorCodes.ORDER_LIMIT_EXCEEDED;
    }
}
