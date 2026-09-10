package com.rbdip.bookstore.product.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.product.model.Product;
import com.rbdip.bookstore.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProductControllerTest {

    private final ProductRepository repository = mock(ProductRepository.class);
    private final ProductController controller = new ProductController(repository);

    @Test
    void createsProductFromRequestBody() {
        Product saved = mock(Product.class);
        when(saved.getId()).thenReturn(11L);
        when(saved.getName()).thenReturn("Clean Code");
        when(repository.save(org.mockito.ArgumentMatchers.any(Product.class))).thenReturn(saved);

        Map<String, Object> result = controller.createProduct(Map.of(
                "name", "Clean Code",
                "price", "35.00",
                "description", "A classic"));

        assertThat(result).containsEntry("id", 11L).containsEntry("name", "Clean Code");
        verify(repository).save(org.mockito.ArgumentMatchers.argThat(product ->
                product.getName().equals("Clean Code")
                        && product.getPrice().compareTo(new BigDecimal("35.00")) == 0
                        && product.getDescription().equals("A classic")));
    }

    @Test
    void listsProductsAsApiPayloads() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(5L);
        when(product.getName()).thenReturn("Refactoring");
        when(product.getPrice()).thenReturn(new BigDecimal("40.00"));
        when(repository.findAll()).thenReturn(List.of(product));

        assertThat(controller.listProducts()).containsExactly(Map.of(
                "id", 5L, "name", "Refactoring", "price", "40.00"));
    }
}
