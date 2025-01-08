package com.tr.demo.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerTierEnums {

    REGULAR("Regular"),
    GOLD("Gold"),
    PLATINUM("Platinum");

    private final String tierName;

    public static CustomerTierEnums fromValue(final String value) {
        for (CustomerTierEnums tier : CustomerTierEnums.values()) {
            if (tier.getTierName().equalsIgnoreCase(value)) {
                return tier;
            }
        }
        return null;
    }

}
