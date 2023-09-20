package com.shepherdmoney.interviewproject.vo.request;

import lombok.Data;

@Data
public class AddCreditCardToUserPayload {

    private int userId;

    private String cardIssuanceBank;

    private String cardNumber;

    public int getUserId() {
        return userId;
    }

    public String getIssuanceBank() {
        return cardIssuanceBank;
    }

    public String getNumber() {
        return cardNumber;
    }
}
