package com.tr.demo.controller;

import com.tr.demo.model.request.LoginRequest;
import com.tr.demo.model.request.RefreshTokenRequest;
import com.tr.demo.model.response.InvalidateTokenResponse;
import com.tr.demo.model.response.JwtAuthenticationResponse;
import com.tr.demo.model.response.ValidateTokenResponse;
import com.tr.demo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@RestController
@RequestMapping("")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam final String token) {
        ValidateTokenResponse build = ValidateTokenResponse.builder()
                .validated(authenticationService.validate(token)).build();
        return ResponseEntity.ok(build.getValidated());
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(
            @RequestBody final LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<InvalidateTokenResponse> logout(
            @RequestHeader(value = AUTHORIZATION) final String authorization) {
        return ResponseEntity.ok(authenticationService.invalidateToken(authorization));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(
            @RequestBody final RefreshTokenRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
}
