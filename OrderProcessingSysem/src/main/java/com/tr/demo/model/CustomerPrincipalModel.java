package com.tr.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPrincipalModel {
    private Long customerId;
    private String customerName;
    private String username;
    private String email;
    private String status;
    private boolean enabled;
    private int orderCount;
    private double discountRate;
}
