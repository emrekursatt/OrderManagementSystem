package com.tr.demo.advice.exception;
import com.tr.demo.advice.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class PasswordNotContainsLetterException extends RuntimeException {

    private final int code;

    public PasswordNotContainsLetterException() {
        super("Password must contain both number and letter!");
        this.code = ErrorCodes.PASSWORD_NOT_CONTAIN_LETTER;
    }
}
