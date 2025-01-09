package com.tr.demo.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateProductResponse {

    private String productName;
    private int stocks;
    private double price;
}
