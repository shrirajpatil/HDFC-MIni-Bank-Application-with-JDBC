package com.hdfc.minibank.Entities;

import com.hdfc.minibank.Exception.InsufficientBalanceException;
import com.hdfc.minibank.enums.AccountType;

import java.math.BigDecimal;

public class SavingsAccount extends Account{


    public static final BigDecimal InterestRate=new BigDecimal(6);
    public static final BigDecimal minimumbalance = new BigDecimal(1000);

    @Override
    public BigDecimal getInterestRate() {
        return InterestRate;
    }

    @Override
    public BigDecimal getMinimalBalance() {
        return minimumbalance;
    }

    @Override
    public BigDecimal getInterest_rate() {
        return null;
    }

    @Override
    public BigDecimal getMinimum_balance() {
        return null;
    }

    public SavingsAccount(String accountNo, String customerId, BigDecimal balance) {
        super(accountNo, customerId, AccountType.SAVINGS, balance);
        setType(AccountType.SAVINGS);
    }
    public void deposit(BigDecimal amount){
        if(amount!=null && amount.compareTo(BigDecimal.ZERO)>0) {
            setBalance(getBalance().add(amount));
        } else{
            throw new IllegalArgumentException("deposit amount must be positive");
        }
    }

    public void withdraw(BigDecimal amount) throws InsufficientBalanceException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (getBalance() == null) {
            throw new IllegalStateException("Account balance is not initialized");
        }
        if (getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }
        setBalance(getBalance().subtract(amount));
    }

    public void transfer(Account toAccount, BigDecimal amount) throws InsufficientBalanceException {
        if (toAccount == null) {
            throw new IllegalArgumentException("Destination account cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        this.withdraw(amount);

        toAccount.deposit(amount);
    }
    @Override
    public String toString() {
        return "Savings Account{" +
                "accountnum='" + getAccountNo() + '\'' +
                ", CID='" + getCustomerId() + '\'' +
                ", balance=" + getBalance() +
                '}';
    }
}