package com.tr.demo.advice.exception;

import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {

    private final int code;

    public ExternalServiceException(String message, int code) {
        super(message);
        this.code = code;
    }

}
