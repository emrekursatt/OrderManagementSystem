
import com.tr.demo.advice.exception.ChangePasswordMismatchException;
import com.tr.demo.advice.exception.NoSuchCustomerException;
import com.tr.demo.advice.exception.PasswordSameWithIdException;
import com.tr.demo.advice.exception.ResetPasswordStatusException;
import com.tr.demo.entity.CustomerEntity;
import com.tr.demo.model.request.ChangePasswordRequest;
import com.tr.demo.repository.CustomerEntityRepository;
import com.tr.demo.security.CustomerPrincipal;
import com.tr.demo.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PasswordServiceTest {

    @InjectMocks
    private PasswordService passwordService;

    @Mock
    private CustomerEntityRepository customerEntityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        // Arrange
        CustomerPrincipal principal = mock(CustomerPrincipal.class);
        when(principal.getId()).thenReturn(1L);
        when(principal.getPassword()).thenReturn("encodedCurrentPassword");

        ChangePasswordRequest request = new ChangePasswordRequest("currentPassword1", "newPassword123");

        CustomerEntity customerEntity = CustomerEntity.builder()
                .id(1L)
                .status(1) // Active status
                .password("encodedCurrentPassword")
                .build();

        when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
        when(passwordEncoder.matches("currentPassword1", "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        // Act
        passwordService.changePassword(principal, request);

        verify(customerEntityRepository, times(1)).save(argThat(customer ->
                customer.getPassword().equals("encodedNewPassword")));
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        // Arrange
        CustomerPrincipal principal = mock(CustomerPrincipal.class);
        when(principal.getId()).thenReturn(1L);

        ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "newValidPassword");

        when(customerEntityRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchCustomerException.class, () -> passwordService.changePassword(principal, request));
    }
}
