package com.hdfc.minibank.dao;

import com.hdfc.minibank.Entities.Account;
import com.hdfc.minibank.enums.AccountType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    private final Connection conn;

    public AccountDAO(Connection conn) {
        this.conn = conn;
    }

    public Account getAccountByNumber(String accountNo) throws SQLException {
        String sql = "SELECT * FROM ACCOUNT WHERE account_no = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String accNo = rs.getString("account_no");
                String customerId = rs.getString("customer_id");
                String accTypeStr = rs.getString("account_type");
                BigDecimal balance = rs.getBigDecimal("balance");

                AccountType type = AccountType.valueOf(accTypeStr.toUpperCase());

                // Depending on your project structure, return the right subclass
                switch (type) {
                    case SAVINGS:
                        return new com.hdfc.minibank.Entities.SavingsAccount(accNo, customerId, balance);
                    case CURRENT:
                        return new com.hdfc.minibank.Entities.CurrentAccount(accNo, customerId, balance);
                    default:
                        throw new IllegalArgumentException("Unknown account type: " + accTypeStr);
                }
            }
        }

        return null;
    }

    public void updateBalance(Account account) throws SQLException {
        String sql = "UPDATE ACCOUNT SET balance = ? WHERE account_no = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, account.getBalance());
            stmt.setString(2, account.getAccountNo());
            stmt.executeUpdate();
        }
    }

    public void createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO ACCOUNT (account_no, customer_id, account_type, balance) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, account.getAccountNo());
            stmt.setString(2, account.getCustomerId());
            stmt.setString(3, account.getType().name());
            stmt.setBigDecimal(4, account.getBalance());
            stmt.executeUpdate();
        }
    }
}