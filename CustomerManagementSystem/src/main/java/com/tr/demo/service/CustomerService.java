package com.tr.demo.service;

import com.tr.demo.advice.exception.NoSuchCustomerException;
import com.tr.demo.entity.CustomerEntity;
import com.tr.demo.entity.TiersEntity;
import com.tr.demo.entity.TiersHistoryEntity;
import com.tr.demo.model.enums.CustomerStatusEnums;
import com.tr.demo.model.enums.CustomerTierEnums;
import com.tr.demo.model.response.UserAllResponse;
import com.tr.demo.repository.CustomerEntityRepository;
import com.tr.demo.repository.TiersEntityRepository;
import com.tr.demo.repository.TiersHistoryEntityRepository;
import com.tr.demo.security.CustomerPrincipal;
import com.tr.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerEntityRepository customerEntityRepository;
    private final TiersEntityRepository tiersEntityRepository;
    private final TiersHistoryEntityRepository tiersHistoryEntityRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void resetFailureLoginCount(Long userId) {
        CustomerEntity byId = customerEntityRepository
                .findById(userId)
                .orElseThrow(NoSuchCustomerException::new);
        customerEntityRepository.save(byId);
        log.info("Customer {} has been reset the failure login count", byId.getUsername());
    }

    public void increaseFailureLoginCount(Long userId) {
        CustomerEntity byId = customerEntityRepository
                .findById(userId)
                .orElseThrow(NoSuchCustomerException::new);
        int currentFails = Objects.nonNull(byId.getFailLoginCount()) ? byId.getFailLoginCount() : 0;
        byId.setFailLoginCount(currentFails + 1);
        customerEntityRepository.save(byId);
    }

    public UserAllResponse getUserByUserName(String userName) {
        CustomerEntity usersEntity = customerEntityRepository.findByUsername(userName).orElseThrow(NoSuchCustomerException::new);
        return UserAllResponse.builder()
                .userName(usersEntity.getUsername())
                .email(usersEntity.getEmail())
                .enabled(usersEntity.getEnabled())
                .status(CustomerStatusEnums.fromValue(usersEntity.getStatus()).name())
                .build();
    }

    public void incrementOrderCount(Long customerId) {
        CustomerEntity customer = customerEntityRepository.findById(customerId)
                .orElseThrow(NoSuchCustomerException::new);

        customer.setOrderCount(customer.getOrderCount() == null ? 0 : customer.getOrderCount()  + 1);

        Optional<TiersEntity> currentTierEntity = Optional.of(customer.getTiersEntity());
        String currentTierName = customer.getTiersEntity().getName();

        if(customer.getOrderCount() >= 10 && customer.getOrderCount() < 20) {
            tiersEntityRepository.findByName(CustomerTierEnums.GOLD.getTierName())
                    .ifPresent(customer::setTiersEntity);
            log.info("Customer {} has been promoted to Gold tier", customer.getUsername());
        } else if (customer.getOrderCount() >= 20) {
            tiersEntityRepository.findByName(CustomerTierEnums.PLATINUM.getTierName())
                    .ifPresent(customer::setTiersEntity);
            log.info("Customer {} has been promoted to Platinum tier", customer.getUsername());
        }
        customerEntityRepository.save(customer);

        if (!currentTierName.equals(customer.getTiersEntity().getName())) {
            tiersHistoryEntityRepository.save(TiersHistoryEntity.builder()
                    .customerEntity(customer)
                    .previousTierEntity(currentTierEntity.get())
                    .newTiersEntity(customer.getTiersEntity())
                    .changeDate(OffsetTime.now())
                    .build());

            jwtTokenProvider.updateCustomerPrincipalInRedis(customer);
        }
    }
    public void notifyCustomers() {
        List<CustomerEntity> customers = customerEntityRepository.findAllByOrderCount(9);
        customers.forEach(customer -> log.info("log.info(You have placed 9 orders with us. Buy one more stuff and you will be promoted to Gold customer and enjoy 10% discounts!) Customer :  {}", customer.getUsername()));
        System.out.println("Sent mail to customers ");
    }
}
