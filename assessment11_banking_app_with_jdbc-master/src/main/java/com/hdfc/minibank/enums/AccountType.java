package com.hdfc.minibank.enums;

public enum AccountType {
    SAVINGS("Savings Account"),
    CURRENT("Current Account");

    private String displayName = "";

    AccountType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }

//    public String toString{
//        return displayName;
//    }
}