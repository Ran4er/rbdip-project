package com.rbdip.bookstore.order.repository;

import com.rbdip.bookstore.order.model.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findFirstByFirstNameAndLastNameAndAddressAndPhone(
            String firstName, String lastName, String address, String phone);
}
