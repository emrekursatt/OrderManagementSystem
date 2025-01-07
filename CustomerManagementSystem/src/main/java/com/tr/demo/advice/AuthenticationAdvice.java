package com.tr.demo.advice;

import com.tr.demo.advice.constants.ErrorCodes;
import com.tr.demo.advice.exception.*;
import com.tr.demo.model.error.Error;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AuthenticationAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception e) {
        log.error("An unknown exception occurred!", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(constructError(ErrorCodes.UNKNOWN_ERROR, e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handleException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(constructError(ErrorCodes.BAD_CREDENTIALS, e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Error> handleException(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(ErrorCodes.INVALID_PARAMETER, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(ErrorCodes.INVALID_PARAMETER, e.getMessage()));
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<Error> handleException(ExternalServiceException e) {
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
                .body(constructError(e.getCode(), e.getMessage()));
    }


    @ExceptionHandler(ChangePasswordMismatchException.class)
    public ResponseEntity<Error> handleException(ChangePasswordMismatchException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidAuthTypeException.class)
    public ResponseEntity<Error> handleException(InvalidAuthTypeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<Error> handleException(NoSuchUserException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<Error> handleException(UserAlreadyRegisteredException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(constructError(e.getCode(), e.getMessage()));
    }


    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<Error> handleException(UserNotActiveException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(constructError(e.getCode(), e.getMessage()));
    }


    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Error> handleException(InvalidRefreshTokenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(e.getCode(), e.getMessage()));
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Error> handleException(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(ErrorCodes.NO_SUCH_USER, e.getMessage()));
    }


    @ExceptionHandler(ResetPasswordStatusException.class)
    public ResponseEntity<Error> handleException(ResetPasswordStatusException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(PasswordHasNumberSequenceException.class)
    public ResponseEntity<Error> handleException(PasswordHasNumberSequenceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(PasswordLengthException.class)
    public ResponseEntity<Error> handleException(PasswordLengthException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(PasswordNotContainsDigitException.class)
    public ResponseEntity<Error> handleException(PasswordNotContainsDigitException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(PasswordNotContainsLetterException.class)
    public ResponseEntity<Error> handleException(PasswordNotContainsLetterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(PasswordSameWithIdException.class)
    public ResponseEntity<Error> handleException(PasswordSameWithIdException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(UserIsBlockedException.class)
    public ResponseEntity<Error> handleException(UserIsBlockedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(constructError(ErrorCodes.USER_BLOCKED, e.getMessage()));
    }

    @ExceptionHandler(UserIsNotActiveException.class)
    public ResponseEntity<Error> handleException(UserIsNotActiveException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(constructError(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<Error> handleException(InternalAuthenticationServiceException e) {
        final Throwable cause = e.getCause();
        if (cause instanceof UserNotActiveException) {
            final UserNotActiveException userNotActiveException = (UserNotActiveException) cause;
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(constructError(ErrorCodes.NOT_ACTIVE_USER, userNotActiveException.getMessage()));
        } else if (cause instanceof InvalidAuthTypeException) {
            final InvalidAuthTypeException invalidAuthTypeException = (InvalidAuthTypeException) cause;
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(constructError(ErrorCodes.INVALID_AUTH_TYPE, invalidAuthTypeException.getMessage()));
        } else if (cause instanceof PasswordChangeRequiredException) {
            final PasswordChangeRequiredException passwordChangeRequiredException = (PasswordChangeRequiredException) cause;
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(constructError(ErrorCodes.PASSWORD_CHANGE_REQUIRED, passwordChangeRequiredException.getMessage()));
        } else if (cause instanceof PasswordChangeSecurityRequiredException) {
            final PasswordChangeSecurityRequiredException securityRequiredException = (PasswordChangeSecurityRequiredException) cause;
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(constructError(ErrorCodes.PASSWORD_CHANGE_REQUIRED_SEC, securityRequiredException.getMessage()));
        } else if (cause instanceof UserIsBlockedException) {
            final UserIsBlockedException securityRequiredException = (UserIsBlockedException) cause;
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(constructError(ErrorCodes.USER_BLOCKED, securityRequiredException.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(constructError(ErrorCodes.UNKNOWN_ERROR, e.getMessage()));
    }

    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<Error> handleException(UserNotEnabledException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(constructError(ErrorCodes.NOT_ENABLED_USER, e.getMessage()));
    }

    private Error constructError(final int code, final String message) {
        return Error.builder()
                .code(code)
                .message(message)
                .timestamp(new DateTime().getMillis())
                .build();
    }
}
