package com.rbdip.bookstore.integration.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.order.model.Order;
import com.rbdip.bookstore.order.model.OrderItem;
import com.rbdip.bookstore.order.repository.OrderItemRepository;
import com.rbdip.bookstore.order.repository.OrderRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderPurchaseVerificationAdapterTest {

    @Test
    void requiresBothOrdersAndItemsToReportRecordedPurchases() {
        OrderRepository orders = mock(OrderRepository.class);
        OrderItemRepository items = mock(OrderItemRepository.class);
        OrderPurchaseVerificationAdapter adapter = new OrderPurchaseVerificationAdapter(orders, items);

        when(orders.findAll()).thenReturn(List.of());
        assertThat(adapter.hasRecordedPurchases()).isFalse();

        when(orders.findAll()).thenReturn(List.of(mock(Order.class)));
        when(items.findAll()).thenReturn(List.of());
        assertThat(adapter.hasRecordedPurchases()).isFalse();

        when(items.findAll()).thenReturn(List.of(mock(OrderItem.class)));
        assertThat(adapter.hasRecordedPurchases()).isTrue();
    }
}
