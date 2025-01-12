
import com.tr.demo.model.request.LoginRequest;
import com.tr.demo.model.response.JwtAuthenticationResponse;
import com.tr.demo.security.CustomerPrincipal;
import com.tr.demo.security.JwtTokenProvider;
import com.tr.demo.service.AuthenticationService;
import com.tr.demo.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnJwtTokenForValidCredentials() {
        LoginRequest loginRequest = LoginRequest.builder()
                .userName("testUser")
                .password("password123")
                .build();

        Authentication authenticationMock = mock(Authentication.class);
        CustomerPrincipal customerPrincipal = mock(CustomerPrincipal.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(customerPrincipal);
        when(jwtTokenProvider.generateToken(authenticationMock)).thenReturn("testAccessToken");
        when(jwtTokenProvider.generateRefreshToken(customerPrincipal)).thenReturn("testRefreshToken");

        JwtAuthenticationResponse response = authenticationService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("testAccessToken", response.getAccessToken());
        assertEquals("testRefreshToken", response.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldThrowExceptionForInvalidCredentials() {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
                .userName("testUser")
                .password("wrongPassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginRequest));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
