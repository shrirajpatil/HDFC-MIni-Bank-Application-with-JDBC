package com.hdfc.minibank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/minibank";
    private static final String USER = "admin";      // your DB user
    private static final String PASSWORD = "admin";  // your DB password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
