package com.rbdip.bookstore.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.rbdip.bookstore.order.CreateOrderRequest;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderValidatorTest {

    private final OrderValidator validator = new OrderValidator();

    @Test
    void acceptsValidRequestAndDefaultsQuantity() {
        CreateOrderRequest request = request(
                "Ivan Petrov", "Address", List.of(new CreateOrderRequest.Item(1L, null)));

        validator.validate(request);

        assertThat(validator.normalizeQuantity(null)).isEqualTo(1);
        assertThat(validator.normalizeQuantity(3)).isEqualTo(3);
    }

    @Test
    void rejectsMissingCustomerName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> validator.validate(request(" ", "Address", List.of(item()))))
                .withMessage("customerFullName is required");
    }

    @Test
    void rejectsMissingAddress() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> validator.validate(request("Ivan Petrov", null, List.of(item()))))
                .withMessage("customerAddress is required");
    }

    @Test
    void rejectsMissingItems() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> validator.validate(request("Ivan Petrov", "Address", List.of())))
                .withMessage("order must contain at least one item");
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> validator.normalizeQuantity(0))
                .withMessage("quantity must be positive");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> validator.normalizeQuantity(-1))
                .withMessage("quantity must be positive");
    }

    private CreateOrderRequest request(
            String name, String address, List<CreateOrderRequest.Item> items) {
        return new CreateOrderRequest(name, address, null, "regular", null, items);
    }

    private CreateOrderRequest.Item item() {
        return new CreateOrderRequest.Item(1L, 1);
    }
}
