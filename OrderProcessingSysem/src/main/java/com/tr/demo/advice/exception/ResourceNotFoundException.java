package com.tr.demo.advice.exception;

import com.tr.demo.advice.constans.ErrorCodes;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final int code;

    public ResourceNotFoundException() {
        super("Product Not Found");
        this.code = ErrorCodes.NOT_FOUND;
    }
}
