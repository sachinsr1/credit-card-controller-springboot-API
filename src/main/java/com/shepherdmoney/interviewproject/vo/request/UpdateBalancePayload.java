package com.shepherdmoney.interviewproject.vo.request;

import java.time.Instant;

import lombok.Data;

@Data
public class UpdateBalancePayload {

    private String creditCardNumber;
    
    private Instant transactionTime;

    private double transactionAmount;

    
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    
    public Instant getTransactionTime() {
        return transactionTime;
    }

   
    public double getTransactionAmount() {
        return transactionAmount;
    }
}
