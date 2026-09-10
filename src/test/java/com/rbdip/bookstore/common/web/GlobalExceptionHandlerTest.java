package com.rbdip.bookstore.common.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    @Test
    void turnsIllegalArgumentsIntoBadRequests() {
        ResponseEntity<Map<String, String>> response =
                new GlobalExceptionHandler().handleValidation(new IllegalArgumentException("invalid order"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "invalid order");
    }
}
