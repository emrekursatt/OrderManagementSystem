package com.tr.demo.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProducRequest {

    private String productName;
    private double price;
    private int stocks;
}
