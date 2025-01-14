package com.tr.demo.security;

import com.tr.demo.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.tr.demo.advice.constants.UserServiceConstants.X_CHANNEL_TYPE;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        final String jwtToken = tokenProvider.extractTokenFromRequest(request);
        if (StringUtils.isNotEmpty(jwtToken) && tokenProvider.validateToken(jwtToken)) {
            final Long id = tokenProvider.extractMemberNoFromToken(jwtToken);
            final String channel = tokenProvider.extractChannelFromToken(jwtToken);
            final CustomerPrincipal userPrincipal = customUserDetailsService.loadUserById(id);
            final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            response.addHeader(X_CHANNEL_TYPE, channel);
        }
        filterChain.doFilter(request, response);
    }
}
