package com.tr.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRabbitMessage {
    private Long orderId;
    private Long customerId;
    private Double totalAmount;
}
