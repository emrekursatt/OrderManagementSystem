package com.tr.demo.advice.exception;

import com.tr.demo.advice.constants.ErrorCodes;
import com.tr.demo.model.enums.CustomerStatusEnums;
import lombok.Getter;

@Getter
public class CustomerNotActiveException extends RuntimeException {

    private final int code;

    public CustomerNotActiveException(int userStatus) {
        super("User is not active. Current user status : " + CustomerStatusEnums.fromValue(userStatus));
        this.code = ErrorCodes.NOT_ACTIVE_CUSTOMER;
    }
}
