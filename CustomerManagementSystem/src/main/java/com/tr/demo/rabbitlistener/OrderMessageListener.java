package com.tr.demo.rabbitlistener;

import com.tr.demo.model.OrderRabbitMessage;
import com.tr.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMessageListener {

    private final CustomerService customerService;

    @RabbitListener(queues = "order.queue")
    public void handleOrderMessage(OrderRabbitMessage message) {
        customerService.incrementOrderCount(message.getCustomerId());
    }
}
