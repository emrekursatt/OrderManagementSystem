package com.tr.demo.service;

import com.tr.demo.advice.exception.*;
import com.tr.demo.security.CustomerPrincipal;
import com.tr.demo.entity.CustomerEntity;
import com.tr.demo.model.enums.CustomerStatusEnums;
import com.tr.demo.repository.CustomerEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerEntityRepository customerEntityRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String value) throws UsernameNotFoundException {
        final CustomerEntity byCustomerId = customerEntityRepository.findByUsername(value)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username : " + value));
        return createPrincipal(byCustomerId);
    }

    @Transactional
    public CustomerPrincipal loadUserById(final Long id) {
        CustomerEntity usersEntity = customerEntityRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));
        return CustomerPrincipal.constructCustomerPrincipal(usersEntity);
    }

    private UserDetails createPrincipal(final CustomerEntity usersEntity) {
        final CustomerPrincipal userPrincipal = CustomerPrincipal.constructCustomerPrincipal(usersEntity);
        assertUserStatusIsAllowed(usersEntity);
        return userPrincipal;
    }

    private void assertUserStatusIsAllowed(CustomerEntity usersEntity) {
        int status = usersEntity.getStatus();
        Boolean enabled = usersEntity.getEnabled();

        if (enabled == null || !enabled) {
            throw new UserNotEnabledException();
        }
        if (CustomerStatusEnums.isPasswordChangeRequiredForSecurity(status)) {
            throw new PasswordChangeSecurityRequiredException();
        }
        if (CustomerStatusEnums.isPasswordChangeRequired(status)) {
            throw new PasswordChangeRequiredException();
        }
        if (CustomerStatusEnums.BLOCKED.getStatus().equals(status)) {
            throw new UserIsBlockedException();
        }
        if (!CustomerStatusEnums.isUserActive(status)) {
            throw new UserNotActiveException(status);
        }
    }
}
