package com.tr.demo.notification;

import com.tr.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TierNotifications {

//    private final CustomerService customerService;
//
//
//    private static final String CRON_EVERY_10_SECONDS = "0/10 * * * * *";
//
//    @Scheduled(cron = CRON_EVERY_10_SECONDS)
//    public void notifyCustomers() {
//        log.info("Notifying customers");
//        customerService.notifyCustomers();
//    }
}
