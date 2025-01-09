package com.tr.demo.model.response;

import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

@Data
@Builder
public class CreateOrderResponse {

    private String customerName;
    private DateTime orderDate;
    private double totalAmount;
    private String paymentType;
}
