package com.hdfc.minibank.dao;

import com.hdfc.minibank.Entities.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class CustomerDAO {

    private final Connection conn;  // ✅ store the connection

    // constructor to initialize connection
    public CustomerDAO(Connection conn) {
        this.conn = conn;
    }

    public void createCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO CUSTOMER (customer_id, name, email, phone, dob) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getCustomerId());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setDate(5, java.sql.Date.valueOf(LocalDate.parse(customer.getDateOfBirth())));
            stmt.executeUpdate();
        }
    }
}
