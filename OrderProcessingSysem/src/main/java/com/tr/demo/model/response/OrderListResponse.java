package com.tr.demo.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderListResponse {

    private List<CreateOrderResponse> orders;
}
