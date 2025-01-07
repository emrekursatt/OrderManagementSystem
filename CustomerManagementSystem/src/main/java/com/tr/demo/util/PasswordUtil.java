package com.tr.demo.util;

import com.tr.demo.advice.exception.PasswordLengthException;
import com.tr.demo.advice.exception.PasswordNotContainsDigitException;
import com.tr.demo.advice.exception.PasswordNotContainsLetterException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordUtil {
    public static void assertPasswordIsValid(String password) {
        checkPasswordLength(password);
        checkAlphaNumeric(password);
    }

    private static void checkAlphaNumeric(String password) {
        if (!Pattern.matches(".*[0-9].*", password)) {
            throw new PasswordNotContainsDigitException();
        }
        if (!Pattern.matches(".*[A-Za-z].*", password)) {
            throw new PasswordNotContainsLetterException();
        }
    }

    private static void checkPasswordLength(String password) {
        if (password.length() < 8 || password.length() > 20) {
            throw new PasswordLengthException();
        }
    }
}
