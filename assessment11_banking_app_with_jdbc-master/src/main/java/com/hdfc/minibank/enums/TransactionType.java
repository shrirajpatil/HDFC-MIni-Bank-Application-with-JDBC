package com.hdfc.minibank.enums;

public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWL("Withdraw"),
    TRANSFER("Transfer");

    private final String displayName;

    TransactionType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString(){
        return "TransactionType{" +
                "displayName='" + displayName + '\'' +
                '}';
    }
}