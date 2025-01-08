package com.tr.demo.security;

import com.tr.demo.service.CustomUserDetailsService;
import com.tr.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationListener {

    private final CustomerService customerService;
    private final CustomUserDetailsService customUserDetailsService;

    @EventListener
    public void publishAuthenticationSuccess(ResetFailLoginCount event) {
        customerService.resetFailureLoginCount(event.getUserId());
    }

    @EventListener
    public void publishAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        CustomerPrincipal userDetails = (CustomerPrincipal) customUserDetailsService.loadUserByUsername(username);
        customerService.increaseFailureLoginCount(userDetails.getId());
    }
}
