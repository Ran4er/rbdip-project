package com.rbdip.bookstore.order.repository;

import com.rbdip.bookstore.order.model.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select i from OrderItem i join fetch i.product where i.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
}
