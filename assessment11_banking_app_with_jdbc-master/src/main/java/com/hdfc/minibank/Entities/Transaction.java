package com.hdfc.minibank.Entities;

import com.hdfc.minibank.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


public class Transaction {
    private String transactionId;
    private BigDecimal amount;
    private String accountNo;
    private LocalDateTime timestamp;
    private TransactionType type;
    private String toAccountNo;

    public Transaction()
    {}

    // Parameterized
    public Transaction(String transactionId, String accountNo, TransactionType type, BigDecimal amount, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.accountNo = accountNo;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Constructor for Transfer
    public Transaction(String transactionId, BigDecimal amount, String fromAccountNo, LocalDateTime timestamp, TransactionType type, String toAccountNo) {
        this.transactionId = transactionId;
        this.accountNo = fromAccountNo; // make sure to set this
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.toAccountNo = toAccountNo;
    }



    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getToAccountNo() {
        return toAccountNo;
    }

    public void setToAccountNo(String toAccountNo) {
        this.toAccountNo = toAccountNo;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", accountNo='" + accountNo + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                '}';

    }

    @Override
    public int hashCode()
    {
        return Objects.hash(transactionId);
    }
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
        {
            return false;
        }
        if(this == obj)
        {
            return true;
        }
        Transaction transaction = (Transaction) obj;

        return Objects.equals(transactionId, transaction.transactionId);
    }



}