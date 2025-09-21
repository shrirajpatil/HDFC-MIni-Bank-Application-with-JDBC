package com.hdfc.minibank.dao;

import com.hdfc.minibank.Entities.Transaction;
import com.hdfc.minibank.enums.TransactionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private final Connection conn;

    public TransactionDAO(Connection conn) {
        this.conn = conn;
    }

    // ✅ Save transaction
    public void saveTransaction(Transaction txn) throws SQLException {
        String sql = "INSERT INTO TRANSACTION (txn_id, account_no, txn_type, amount, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txn.getTransactionId());
            stmt.setString(2, txn.getAccountNo());
            stmt.setString(3, txn.getType().name());  // e.g. DEPOSIT, WITHDRAW
            stmt.setBigDecimal(4, txn.getAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(txn.getTimestamp()));
            stmt.executeUpdate();
        }
    }

    // ✅ Fetch transactions by account number
    public List<Transaction> getTransactionsByAccount(String accountNo) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT txn_id, account_no, txn_type, amount, timestamp FROM TRANSACTION WHERE account_no = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction txn = new Transaction(
                        rs.getString("txn_id"),
                        rs.getString("account_no"),
                        TransactionType.valueOf(rs.getString("txn_type")),
                        rs.getBigDecimal("amount"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                );
                transactions.add(txn);
            }
        }
        return transactions;
    }
}
