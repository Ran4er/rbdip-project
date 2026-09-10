package com.rbdip.bookstore.order.service;

import com.rbdip.bookstore.order.CreateOrderRequest;
import com.rbdip.bookstore.order.PricingCalculator;
import com.rbdip.bookstore.order.model.Order;
import com.rbdip.bookstore.order.repository.OrderRepository;
import com.rbdip.bookstore.product.model.Product;
import com.rbdip.bookstore.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final String REGULAR_CUSTOMER = "regular";

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderValidator validator;
    private final PricingCalculator pricingCalculator;
    private final OrderPersistenceService persistenceService;
    private final OrderNotificationService notificationService;

    public OrderService(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderValidator validator,
            PricingCalculator pricingCalculator,
            OrderPersistenceService persistenceService,
            OrderNotificationService notificationService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.validator = validator;
        this.pricingCalculator = pricingCalculator;
        this.persistenceService = persistenceService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        validator.validate(request);
        PreparedItems preparedItems = prepareItems(request.items());
        String customerType = request.customerType() == null ? REGULAR_CUSTOMER : request.customerType();
        BigDecimal total = pricingCalculator.calculateOrderTotal(
                preparedItems.lineItems(), customerType, request.couponCode());
        Order order = persistenceService.save(request, preparedItems.products(), preparedItems.lineItems());
        notificationService.sendConfirmation(request.customerFullName(), order.getId(), total);
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> listOrders() {
        return orderRepository.findAllWithDetails();
    }

    private PreparedItems prepareItems(List<CreateOrderRequest.Item> requestedItems) {
        List<Product> products = new ArrayList<>();
        List<PricingCalculator.LineItem> lineItems = new ArrayList<>();
        for (CreateOrderRequest.Item requestedItem : requestedItems) {
            Product product = productRepository.findById(requestedItem.productId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "product " + requestedItem.productId() + " not found"));
            int quantity = validator.normalizeQuantity(requestedItem.quantity());
            products.add(product);
            lineItems.add(new PricingCalculator.LineItem(product.getPrice(), quantity));
        }
        return new PreparedItems(products, lineItems);
    }

    private record PreparedItems(List<Product> products, List<PricingCalculator.LineItem> lineItems) {
    }
}
