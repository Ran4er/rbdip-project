package com.rbdip.bookstore.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String address;

    private String phone;

    protected Customer() {
        // for JPA
    }

    public Customer(String fullName, String address, String phone) {
        NameParts name = splitName(fullName);
        this.firstName = name.firstName();
        this.lastName = name.lastName();
        this.address = address;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        if (lastName.isBlank()) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    private static NameParts splitName(String fullName) {
        String normalized = fullName == null ? "" : fullName.trim().replaceAll("\s+", " ");
        int separator = normalized.indexOf(' ');
        if (separator < 0) {
            return new NameParts(normalized, "");
        }
        return new NameParts(normalized.substring(0, separator), normalized.substring(separator + 1));
    }

    private record NameParts(String firstName, String lastName) {
    }
}
