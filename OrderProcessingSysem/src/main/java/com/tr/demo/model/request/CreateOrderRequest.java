package com.tr.demo.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    @NotEmpty
    private List<OrderProductRequest> products;
}
