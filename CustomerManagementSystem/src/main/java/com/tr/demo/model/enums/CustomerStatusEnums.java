package com.tr.demo.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum CustomerStatusEnums {

    REGISTRATION_STARTED(1),
    ACTIVE(2),
    PASSWORD_CHANGE_REQUIRED(3),
    PASSIVE_BY_ADMIN(4),
    PASSWORD_CHANGE_SECURITY(5),
    BLOCKED(-1);

    private Integer status;

    public static CustomerStatusEnums fromValue(final int status) {
        for (CustomerStatusEnums userStatus : CustomerStatusEnums.values()) {
            if (Objects.equals(userStatus.getStatus(), status)) {
                return userStatus;
            }
        }
        throw new RuntimeException("Unknown user status for value : " + status);
    }

    public static boolean isUserActive(final int status) {
        return status == ACTIVE.getStatus();
    }

    public static boolean isPasswordChangeRequired(final int status) {
        return status == PASSWORD_CHANGE_REQUIRED.getStatus();
    }

    public static boolean isPasswordChangeRequiredForSecurity(final int status) {
        return status == PASSWORD_CHANGE_SECURITY.getStatus();
    }

    public static boolean isUserInBlackList(final CustomerStatusEnums status) {
        return status.equals(BLOCKED)
                || status.equals(PASSIVE_BY_ADMIN)
                || status.equals(PASSWORD_CHANGE_SECURITY);
    }
}
