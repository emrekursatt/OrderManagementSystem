package com.tr.demo.configuration.security;

import com.tr.demo.advice.constants.ErrorCodes;
import com.tr.demo.model.error.Error;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static com.tr.demo.util.ErrorResponseUtil.flushError;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest httpServletRequest,
                         final HttpServletResponse httpServletResponse,
                         final AuthenticationException e) throws IOException {
        Error error = Error.builder()
                .code(ErrorCodes.UNAUTHENTICATED)
                .message("Authentication error!")
                .timestamp(new Date().getTime())
                .build();
        flushError(httpServletResponse, error);
    }
}
