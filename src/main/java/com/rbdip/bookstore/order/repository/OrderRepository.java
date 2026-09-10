package com.rbdip.bookstore.order.repository;

import com.rbdip.bookstore.order.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    @Query("select distinct o from Order o")
    List<Order> findAllWithDetails();
}
