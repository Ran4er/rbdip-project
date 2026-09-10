package com.rbdip.bookstore.order.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.product.model.Product;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderModelTest {

    @Test
    void exposesNormalizedCustomerAndProductDataThroughLegacyApi() {
        Customer customer = new Customer("Anna Maria Sidorova", "Nevsky 10", "+79990000000");
        Product product = new Product("Refactoring", new BigDecimal("40.00"), "Book");
        Order order = new Order(customer, "new");

        order.addItem(product, 2);

        assertThat(customer.getFirstName()).isEqualTo("Anna");
        assertThat(customer.getLastName()).isEqualTo("Maria Sidorova");
        assertThat(order.getCustomerFullName()).isEqualTo("Anna Maria Sidorova");
        assertThat(order.getCustomerAddress()).isEqualTo("Nevsky 10");
        assertThat(order.getCustomerPhone()).isEqualTo("+79990000000");
        assertThat(order.getStatus()).isEqualTo("new");
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getProductName()).isEqualTo("Refactoring");
        assertThat(order.getItems().get(0).getProductPrice()).isEqualByComparingTo("40.00");
        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void supportsSingleWordCustomerNames() {
        Customer customer = new Customer("Prince", "Address", null);

        assertThat(customer.getFirstName()).isEqualTo("Prince");
        assertThat(customer.getLastName()).isEmpty();
        assertThat(customer.getFullName()).isEqualTo("Prince");
    }
}
