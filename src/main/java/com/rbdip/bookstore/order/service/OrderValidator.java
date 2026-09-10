package com.rbdip.bookstore.order.service;

import com.rbdip.bookstore.order.CreateOrderRequest;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

    private static final int DEFAULT_QUANTITY = 1;

    public void validate(CreateOrderRequest request) {
        if (request.customerFullName() == null || request.customerFullName().isBlank()) {
            throw new IllegalArgumentException("customerFullName is required");
        }
        if (request.customerAddress() == null || request.customerAddress().isBlank()) {
            throw new IllegalArgumentException("customerAddress is required");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("order must contain at least one item");
        }
    }

    public int normalizeQuantity(Integer quantity) {
        int normalized = quantity == null ? DEFAULT_QUANTITY : quantity;
        if (normalized <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        return normalized;
    }
}
