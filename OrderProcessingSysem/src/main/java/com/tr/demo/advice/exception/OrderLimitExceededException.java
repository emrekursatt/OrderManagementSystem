package com.tr.demo.advice.exception;

import com.tr.demo.advice.constans.ErrorCodes;
import lombok.Getter;

@Getter
public class OrderLimitExceededException extends RuntimeException {
    private final int code;

    public OrderLimitExceededException() {
        super("Order limit exceeded max 9 orders allowed");
        this.code = ErrorCodes.ORDER_LIMIT_EXCEEDED;
    }
}
