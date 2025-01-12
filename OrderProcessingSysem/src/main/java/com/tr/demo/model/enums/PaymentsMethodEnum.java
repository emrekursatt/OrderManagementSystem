package com.tr.demo.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentsMethodEnum {

    CREDIT_CARD("Credit Cart"),
    CASH("Cash"),
    PAYPAL("Paypal"),
    BANK_TRANSFER("Bank Transfer");

    private final String value;


    public static PaymentsMethodEnum fromValue(String value) {
        for (PaymentsMethodEnum paymentMethod : values()) {
            if (paymentMethod.value.equalsIgnoreCase(value)) {
                return paymentMethod;
            }
        }
        return null;
    }

}
