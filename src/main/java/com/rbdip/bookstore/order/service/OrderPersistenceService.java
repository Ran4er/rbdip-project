package com.rbdip.bookstore.order.service;

import com.rbdip.bookstore.order.CreateOrderRequest;
import com.rbdip.bookstore.order.PricingCalculator;
import com.rbdip.bookstore.order.model.Customer;
import com.rbdip.bookstore.order.model.Order;
import com.rbdip.bookstore.order.repository.CustomerRepository;
import com.rbdip.bookstore.order.repository.OrderRepository;
import com.rbdip.bookstore.product.model.Product;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderPersistenceService {

    private static final String NEW_STATUS = "new";

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public OrderPersistenceService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    public Order save(
            CreateOrderRequest request,
            List<Product> products,
            List<PricingCalculator.LineItem> lineItems) {
        Customer customer = findOrCreateCustomer(request);
        Order order = new Order(customer, NEW_STATUS);
        for (int index = 0; index < products.size(); index++) {
            order.addItem(products.get(index), lineItems.get(index).quantity());
        }
        return orderRepository.save(order);
    }

    private Customer findOrCreateCustomer(CreateOrderRequest request) {
        Customer candidate = new Customer(
                request.customerFullName(), request.customerAddress(), request.customerPhone());
        return customerRepository.findFirstByFirstNameAndLastNameAndAddressAndPhone(
                        candidate.getFirstName(),
                        candidate.getLastName(),
                        candidate.getAddress(),
                        candidate.getPhone())
                .orElseGet(() -> customerRepository.save(candidate));
    }
}
