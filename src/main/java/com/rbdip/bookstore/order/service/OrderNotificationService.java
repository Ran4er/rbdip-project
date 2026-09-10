package com.rbdip.bookstore.order.service;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderNotificationService.class);

    public void sendConfirmation(String customerName, Long orderId, BigDecimal total) {
        LOGGER.info("Order confirmation: customer={}, orderId={}, total={}", customerName, orderId, total);
    }
}
