package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class PasswordSameWithIdException extends RuntimeException {

    private final int code;

    public PasswordSameWithIdException() {
        super("Password can not be same with user id");
        this.code = ErrorCodes.PASSWORD_CUSTOMER_ID_SAME;
    }
}
