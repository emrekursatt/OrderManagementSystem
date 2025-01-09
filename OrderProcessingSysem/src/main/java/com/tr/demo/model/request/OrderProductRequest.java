package com.tr.demo.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductRequest {

    private String productName;
    private int quantity;
}