package com.tr.demo.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponse {

    private String customerName;
    private DateTime orderDate;
    private double totalAmount;
    private String paymentType;

    public DateTime getOrderDate() {
        return new DateTime();
    }
}
