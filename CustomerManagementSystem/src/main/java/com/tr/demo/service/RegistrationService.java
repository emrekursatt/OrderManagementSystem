package com.tr.demo.service;

import com.tr.demo.advice.exception.CustomerAlreadyRegisteredException;
import com.tr.demo.entity.CustomerEntity;
import com.tr.demo.entity.TiersEntity;
import com.tr.demo.model.enums.CustomerStatusEnums;
import com.tr.demo.model.enums.CustomerTierEnums;
import com.tr.demo.model.request.RegisterRequest;
import com.tr.demo.repository.CustomerEntityRepository;
import com.tr.demo.repository.TiersEntityRepository;
import com.tr.demo.security.CustomerPrincipal;
import com.tr.demo.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationService {

    private final CustomerEntityRepository customerEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TiersEntityRepository tiersEntityRepository;

    @Transactional
    public String registerUser(RegisterRequest registerRequest, CustomerPrincipal CustomerPrincipal) {
        assertUserIsNotExisted(registerRequest.getUserName());
        PasswordUtil.assertPasswordIsValid(registerRequest.getPassword());
        Date registerDate = DateTime.now().toDate();
        CustomerEntity customerEntity = createUserEntity(registerRequest, registerDate);
        CustomerEntity savedCustomerEntity = customerEntityRepository.save(customerEntity);
        log.info("User with customerId : {} is saved", savedCustomerEntity.getUsername());
        return customerEntity.getUsername();
    }

    private CustomerEntity createUserEntity(RegisterRequest registerRequest, Date registerDate) {

        Optional<TiersEntity> tierEntity = tiersEntityRepository.findByName(CustomerTierEnums.REGULAR.getTierName());
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setEnabled(true);
        customerEntity.setStatus(CustomerStatusEnums.ACTIVE.getStatus());
        customerEntity.setEmail(registerRequest.getEmail());
        customerEntity.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customerEntity.setUsername(registerRequest.getUserName());
        customerEntity.setName(registerRequest.getName());
        customerEntity.setTiersEntity(tierEntity.orElse(null));
        customerEntity.setFailLoginCount(0);
        return customerEntity;
    }

    private void assertUserIsNotExisted(String userName) {
        customerEntityRepository
                .findByUsername(userName)
                .ifPresent(userEntity -> {
                    throw new CustomerAlreadyRegisteredException();
                });
    }
}