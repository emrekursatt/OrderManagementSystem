package com.tr.demo.advice.exception;

import com.tr.demo.advice.constans.ErrorCodes;
import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {
  private final int code;

  public BusinessLogicException() {
    super("Business Logic Error");
    this.code = ErrorCodes.BUSINESS_LOGIC_ERROR;
  }
}
