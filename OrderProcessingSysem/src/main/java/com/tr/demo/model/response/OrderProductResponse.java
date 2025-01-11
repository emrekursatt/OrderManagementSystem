package com.tr.demo.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductResponse {

        private String customerEmail;
        private String productName;
        private int quantity;
        private double price;
        private double totalPrice;
        private double discountRate;
}
