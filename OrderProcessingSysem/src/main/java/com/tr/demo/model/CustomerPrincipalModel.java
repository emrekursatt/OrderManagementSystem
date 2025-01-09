package com.tr.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerPrincipalModel {
    private String customerId;
    private String customerName;
    private String username;
    private String email;
    private String status;
    private boolean enabled;
    private int orderCount;
    private double discountRate;
}
