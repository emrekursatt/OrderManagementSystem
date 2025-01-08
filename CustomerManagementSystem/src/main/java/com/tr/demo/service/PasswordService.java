package com.tr.demo.service;

import com.tr.demo.advice.exception.ChangePasswordMismatchException;
import com.tr.demo.advice.exception.NoSuchCustomerException;
import com.tr.demo.advice.exception.PasswordSameWithIdException;
import com.tr.demo.advice.exception.ResetPasswordStatusException;
import com.tr.demo.entity.CustomerEntity;
import com.tr.demo.model.request.ChangePasswordRequest;
import com.tr.demo.repository.CustomerEntityRepository;
import com.tr.demo.security.CustomerPrincipal;
import com.tr.demo.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.tr.demo.model.enums.CustomerStatusEnums.BLOCKED;
import static com.tr.demo.model.enums.CustomerStatusEnums.PASSIVE_BY_ADMIN;

@RequiredArgsConstructor
@Service
@Slf4j
public class PasswordService {

    private final CustomerEntityRepository customerEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(CustomerPrincipal principal, ChangePasswordRequest changePasswordRequest) {
        CustomerEntity usersEntity = customerEntityRepository.findById(principal.getId()).orElseThrow(NoSuchCustomerException::new);
        assertUserStatus(usersEntity.getStatus());
        PasswordUtil.assertPasswordIsValid(changePasswordRequest.getNewPassword());
        if (String.valueOf(principal.getId()).equalsIgnoreCase(changePasswordRequest.getNewPassword())) {
            throw new PasswordSameWithIdException();
        }
        assertCurrentPasswordIsTrue(principal, changePasswordRequest);
        usersEntity.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        customerEntityRepository.save(usersEntity);

    }

    private void assertUserStatus(Integer status) {
        final List<Integer> userStatuses = Arrays.asList(BLOCKED.getStatus(),
                PASSIVE_BY_ADMIN.getStatus());
        if (userStatuses.contains(status)) {
            throw new ResetPasswordStatusException();
        }
    }

    private void assertCurrentPasswordIsTrue(CustomerPrincipal principal, ChangePasswordRequest changePasswordRequest) {
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), principal.getPassword())) {
            throw new ChangePasswordMismatchException();
        }
    }
}
