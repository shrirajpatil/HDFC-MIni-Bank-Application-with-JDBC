package com.hdfc.minibank.Entities;

import com.hdfc.minibank.enums.AccountType;

import java.math.BigDecimal;
import java.util.Objects;
import com.hdfc.minibank.Exception.*;
public abstract class Account {
    private String accountNo;
    private String customerId;
    private AccountType type;
    private BigDecimal balance;

    public Account(){

        this.balance = BigDecimal.ZERO;
    }

    public Account(String accountNo, String customerId, AccountType type, BigDecimal balance) {
        this.accountNo = accountNo;
        this.customerId = customerId;
        this.type = type;
        this.balance = balance;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public abstract BigDecimal getInterestRate();

    public abstract BigDecimal getMinimalBalance();

    public abstract BigDecimal getInterest_rate();

    public abstract BigDecimal getMinimum_balance();

    @Override
    public String toString() {
        return "Account{" +
                "accountNo='" + accountNo + '\'' +
                ", customerId='" + customerId + '\'' +
                ", type=" + type +
                ", balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        Account account = (Account) obj;
        return Objects.equals(accountNo,account.accountNo);
    }

    @Override
    public int hashCode(){
        return Objects.hash(accountNo);
    }

    public synchronized void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositValueException("Deposit value must be greater than 0");
        }

        this.balance = this.balance.add(amount);
    }


    public synchronized void withdraw(BigDecimal amount) throws InsufficientBalanceException {
        if(amount.compareTo(balance)>0){
            throw new InsufficientBalanceException("Insufficient Balance");
        }
        this.balance = this.balance.subtract(amount);
    }




}