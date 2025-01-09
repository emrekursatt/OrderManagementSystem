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

    private Long customerId;
    @NotEmpty
    private List<OrderProductRequest> products;
    @NonNull
    private String paymentMethod;
}
