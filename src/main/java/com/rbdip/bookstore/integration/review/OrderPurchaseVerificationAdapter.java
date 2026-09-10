package com.rbdip.bookstore.integration.review;

import com.rbdip.bookstore.order.repository.OrderItemRepository;
import com.rbdip.bookstore.order.repository.OrderRepository;
import com.rbdip.bookstore.review.spi.PurchaseVerification;
import org.springframework.stereotype.Component;

@Component
public class OrderPurchaseVerificationAdapter implements PurchaseVerification {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderPurchaseVerificationAdapter(
            OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public boolean hasRecordedPurchases() {
        return !orderRepository.findAll().isEmpty() && !orderItemRepository.findAll().isEmpty();
    }
}
