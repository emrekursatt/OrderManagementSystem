package com.tr.demo.service;

import com.tr.demo.advice.exception.NoSuchCustomerException;
import com.tr.demo.entity.CustomerEntity;
import com.tr.demo.model.enums.CustomerStatusEnums;
import com.tr.demo.model.response.UserAllResponse;
import com.tr.demo.repository.CustomerEntityRepository;
import com.tr.demo.security.CustomerPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerEntityRepository customerEntityRepository;

    public void resetFailureLoginCount(Long userId) {
        CustomerEntity byId = customerEntityRepository
                .findById(userId)
                .orElseThrow(NoSuchCustomerException::new);
        customerEntityRepository.save(byId);
    }

    public void activateStatus(CustomerPrincipal customerPrincipal, String userName) {
        CustomerEntity usersEntity = customerEntityRepository.findByUsername(userName).orElseThrow(NoSuchCustomerException::new);
        usersEntity.setStatus(CustomerStatusEnums.ACTIVE.getStatus());
        customerEntityRepository.save(usersEntity);
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
        customer.setOrderCount(customer.getOrderCount() + 1);
        customerEntityRepository.save(customer);
    }
}
