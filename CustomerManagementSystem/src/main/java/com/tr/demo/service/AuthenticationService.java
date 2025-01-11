package com.tr.demo.service;

import com.tr.demo.advice.exception.InvalidRefreshTokenException;
import com.tr.demo.model.request.LoginRequest;
import com.tr.demo.model.request.RefreshTokenRequest;
import com.tr.demo.model.response.InvalidateTokenResponse;
import com.tr.demo.security.CustomerPrincipal;
import com.tr.demo.security.JwtTokenProvider;
import com.tr.demo.model.response.JwtAuthenticationResponse;
import com.tr.demo.security.ResetFailLoginCount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.tr.demo.advice.constants.UserServiceConstants.BEARER;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public JwtAuthenticationResponse authenticate(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final CustomerPrincipal customerPrincipal = (CustomerPrincipal) authentication.getPrincipal();
        publishResetFailLoginCountEvent(customerPrincipal);

        return JwtAuthenticationResponse.builder()
                .tokenType(BEARER)
                .accessToken(jwtTokenProvider.generateToken(authentication))
                .refreshToken(jwtTokenProvider.generateRefreshToken(customerPrincipal))
                .build();
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest request) {
        CustomerPrincipal userPrincipal = (CustomerPrincipal) customUserDetailsService.loadUserByUsername(request.getUserName());
        if (jwtTokenProvider.validateRefreshToken(request.getRefreshToken(), userPrincipal.getId())) {
            return JwtAuthenticationResponse.builder()
                    .tokenType(BEARER)
                    .accessToken(jwtTokenProvider.getAccessToken(userPrincipal.getId(), jwtTokenProvider.populateClaims(userPrincipal)))
                    .refreshToken(jwtTokenProvider.generateRefreshToken(userPrincipal))
                    .build();
        }
        throw new InvalidRefreshTokenException();
    }

    public InvalidateTokenResponse invalidateToken(final String authorization) {
        final String token = jwtTokenProvider.extractTokenFromAuthorizationHeader(authorization);
        return InvalidateTokenResponse.builder()
                .loggedOut(jwtTokenProvider.invalidateTokens(token)).build();
    }

    public boolean validate(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    private void publishResetFailLoginCountEvent(CustomerPrincipal customerPrincipal) {
        if (Objects.nonNull(customerPrincipal.getFailLoginCount()) && customerPrincipal.getFailLoginCount() > 0) {
            ResetFailLoginCount event = new ResetFailLoginCount(this, customerPrincipal.getId());
            applicationEventPublisher.publishEvent(event);
        }
    }
}
