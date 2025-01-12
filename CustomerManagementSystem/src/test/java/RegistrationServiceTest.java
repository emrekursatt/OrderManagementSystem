import com.tr.demo.advice.exception.CustomerAlreadyRegisteredException;
import com.tr.demo.advice.exception.PasswordLengthException;
import com.tr.demo.entity.CustomerEntity;
import com.tr.demo.entity.TiersEntity;
import com.tr.demo.model.enums.CustomerTierEnums;
import com.tr.demo.model.request.RegisterRequest;
import com.tr.demo.repository.CustomerEntityRepository;
import com.tr.demo.repository.TiersEntityRepository;
import com.tr.demo.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private CustomerEntityRepository customerEntityRepository;

    @Mock
    private TiersEntityRepository tiersEntityRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterSuccessfully() {
        RegisterRequest request = RegisterRequest.builder()
                .userName("testUser")
                .password("ValidPass123")
                .email("test@example.com")
                .name("Test User")
                .build();

        TiersEntity regularTier = TiersEntity.builder()
                .name(CustomerTierEnums.REGULAR.getTierName())
                .requiredOrders(0)
                .discountRate(0.0)
                .build();

        CustomerEntity customerToSave = CustomerEntity.builder()
                .username("testUser")
                .email("test@example.com")
                .password("encodedPassword")
                .tiersEntity(regularTier)
                .enabled(true)
                .status(1) // ACTIVE status
                .build();

        when(customerEntityRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(tiersEntityRepository.findByName(CustomerTierEnums.REGULAR.getTierName())).thenReturn(Optional.of(regularTier));
        when(passwordEncoder.encode("ValidPass123")).thenReturn("encodedPassword");
        when(customerEntityRepository.save(any(CustomerEntity.class))).thenReturn(customerToSave);

        String result = registrationService.registerUser(request, null);

        assertEquals("testUser", result);
        verify(customerEntityRepository, times(1)).save(argThat(customer ->
                customer.getTiersEntity().getName().equals(CustomerTierEnums.REGULAR.getTierName())));
    }

    @Test
    void shouldThrowExceptionIfUserExists() {
        RegisterRequest request = RegisterRequest.builder()
                .userName("existingUser")
                .password("Password123")
                .email("existing@example.com")
                .name("Existing User")
                .build();

        CustomerEntity existingCustomer = CustomerEntity.builder().username("existingUser").build();

        when(customerEntityRepository.findByUsername("existingUser")).thenReturn(Optional.of(existingCustomer));

        assertThrows(CustomerAlreadyRegisteredException.class,
                () -> registrationService.registerUser(request, null));
        verify(customerEntityRepository, never()).save(any());
    }

    @Test
    void shouldFailForInvalidPassword() {
        RegisterRequest request = RegisterRequest.builder()
                .userName("testUser")
                .password("1234566") // Invalid password format
                .email("test@example.com")
                .name("Test User")
                .build();

        assertThrows(PasswordLengthException.class,
                () -> registrationService.registerUser(request, null));
        verify(customerEntityRepository, never()).save(any());
    }

    @Test
    void shouldAssignRegularTierByDefault() {
        RegisterRequest request = RegisterRequest.builder()
                .userName("newUser")
                .password("ValidPass123")
                .email("newuser@example.com")
                .name("New User")
                .build();

        TiersEntity regularTier = TiersEntity.builder()
                .name(CustomerTierEnums.REGULAR.getTierName())
                .requiredOrders(0)
                .discountRate(0.0)
                .build();

        CustomerEntity customerToSave = CustomerEntity.builder()
                .username("newUser")
                .email("newuser@example.com")
                .password("encodedPassword")
                .tiersEntity(regularTier)
                .enabled(true)
                .status(1) // ACTIVE status
                .build();

        when(customerEntityRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(tiersEntityRepository.findByName(CustomerTierEnums.REGULAR.getTierName())).thenReturn(Optional.of(regularTier));
        when(passwordEncoder.encode("ValidPass123")).thenReturn("encodedPassword");
        when(customerEntityRepository.save(any(CustomerEntity.class))).thenReturn(customerToSave);

        // Act
        String result = registrationService.registerUser(request, null);

        // Assert
        assertEquals("newUser", result);
        verify(customerEntityRepository, times(1)).save(argThat(customer ->
                customer.getTiersEntity().getName().equals(CustomerTierEnums.REGULAR.getTierName())));
    }
}
