package com.tr.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableScheduling
public class CustomerManagementAppMain {
    public static void main(String[] args) {
        SpringApplication.run(CustomerManagementAppMain.class, args);

    }
}