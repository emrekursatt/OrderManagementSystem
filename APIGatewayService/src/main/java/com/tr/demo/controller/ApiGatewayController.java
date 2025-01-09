package com.tr.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class ApiGatewayController {

    @GetMapping("/fallbackForCustomerService")
    public String fallbackForAuth (){
        return "Customer Service geçici olarak hizmet vermemektedir.";
    }


    @GetMapping("/fallbackForOrderService")
    public String fallbackForExperience (){
        return "Order Service geçici olarak hizmet vermemektedir.";
    }
}
