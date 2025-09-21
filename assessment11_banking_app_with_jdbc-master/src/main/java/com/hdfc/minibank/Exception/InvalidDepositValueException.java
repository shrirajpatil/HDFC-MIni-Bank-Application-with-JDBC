package com.hdfc.minibank.Exception;

public class InvalidDepositValueException extends RuntimeException {
    //private static final String message = "invalid deposit";

    public InvalidDepositValueException(String message) {
        super(message);
    }
}