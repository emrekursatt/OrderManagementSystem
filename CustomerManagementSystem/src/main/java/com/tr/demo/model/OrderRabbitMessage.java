package com.tr.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRabbitMessage  {

    private Long orderId;
    private Long customerId;
    private Double totalAmount;
}
