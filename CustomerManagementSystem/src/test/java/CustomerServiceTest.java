
import com.tr.demo.advice.exception.NoSuchCustomerException;
import com.tr.demo.entity.CustomerEntity;
import com.tr.demo.entity.TiersEntity;
import com.tr.demo.entity.TiersHistoryEntity;
import com.tr.demo.model.enums.CustomerTierEnums;
import com.tr.demo.repository.CustomerEntityRepository;
import com.tr.demo.repository.TiersEntityRepository;
import com.tr.demo.repository.TiersHistoryEntityRepository;
import com.tr.demo.security.JwtTokenProvider;
import com.tr.demo.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerEntityRepository customerEntityRepository;

    @Mock
    private TiersEntityRepository tiersEntityRepository;

    @Mock
    private TiersHistoryEntityRepository tiersHistoryEntityRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void resetFailureLoginCount_shouldThrowExceptionIfUserNotFound() {
        when(customerEntityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchCustomerException.class, () -> customerService.resetFailureLoginCount(1L));
    }

    @Test
    void increaseFailureLoginCount_shouldIncreaseLoginCountSuccessfully() {
        CustomerEntity customer = CustomerEntity.builder()
                .id(1L)
                .failLoginCount(2)
                .username("testUser")
                .build();

        when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.increaseFailureLoginCount(1L);

        assertEquals(3, customer.getFailLoginCount());
        verify(customerEntityRepository, times(1)).save(customer);
    }

    @Test
    void increaseFailureLoginCount_shouldThrowExceptionIfUserNotFound() {
        when(customerEntityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchCustomerException.class, () -> customerService.increaseFailureLoginCount(1L));
    }

    @Test
    void incrementOrderCount_shouldIncreaseOrderCountAndPromoteToGold() {
        CustomerEntity customer = CustomerEntity.builder()
                .id(1L)
                .orderCount(9)
                .tiersEntity(TiersEntity.builder().name(CustomerTierEnums.REGULAR.getTierName()).build())
                .build();

        TiersEntity goldTier = TiersEntity.builder().name(CustomerTierEnums.GOLD.getTierName()).build();

        when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(tiersEntityRepository.findByName(CustomerTierEnums.GOLD.getTierName())).thenReturn(Optional.of(goldTier));

        customerService.incrementOrderCount(1L);

        assertEquals(10, customer.getOrderCount());
        assertEquals(CustomerTierEnums.GOLD.getTierName(), customer.getTiersEntity().getName());
        verify(tiersHistoryEntityRepository, times(1)).save(any(TiersHistoryEntity.class));
        verify(jwtTokenProvider, times(1)).updateCustomerPrincipalInRedis(customer);
    }

    @Test
    void incrementOrderCount_shouldIncreaseOrderCountAndPromoteToPlatinum() {
        CustomerEntity customer = CustomerEntity.builder()
                .id(1L)
                .orderCount(19)
                .tiersEntity(TiersEntity.builder().name(CustomerTierEnums.GOLD.getTierName()).build())
                .build();

        TiersEntity platinumTier = TiersEntity.builder().name(CustomerTierEnums.PLATINUM.getTierName()).build();

        when(customerEntityRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(tiersEntityRepository.findByName(CustomerTierEnums.PLATINUM.getTierName())).thenReturn(Optional.of(platinumTier));

        customerService.incrementOrderCount(1L);

        assertEquals(20, customer.getOrderCount());
        assertEquals(CustomerTierEnums.PLATINUM.getTierName(), customer.getTiersEntity().getName());
        verify(tiersHistoryEntityRepository, times(1)).save(any(TiersHistoryEntity.class));
        verify(jwtTokenProvider, times(1)).updateCustomerPrincipalInRedis(customer);
    }

    @Test
    void incrementOrderCount_shouldThrowExceptionIfUserNotFound() {
        when(customerEntityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchCustomerException.class, () -> customerService.incrementOrderCount(1L));
    }

    @Test
    void notifyCustomers_shouldLogCustomersWithOrderCount9() {
        CustomerEntity customer1 = CustomerEntity.builder().id(1L).username("testUser1").orderCount(9).build();
        CustomerEntity customer2 = CustomerEntity.builder().id(2L).username("testUser2").orderCount(9).build();

        when(customerEntityRepository.findAllByOrderCount(9)).thenReturn(List.of(customer1, customer2));

        customerService.notifyCustomers();

        verify(customerEntityRepository, times(1)).findAllByOrderCount(9);
    }
}
