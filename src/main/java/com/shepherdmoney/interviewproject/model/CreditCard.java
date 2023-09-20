package com.shepherdmoney.interviewproject.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "credit_cards") 
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;

    private String issuanceBank;

    private String number;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "user_id") 
    private User owner;

    public CreditCard(String issuanceBank, String number) {
        this.issuanceBank = issuanceBank;
        this.number = number;
    }

    private double currentBalance;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY) 
    @OrderBy("date ASC")
    private List<BalanceHistory> balanceHistory;

    public void setBalanceHistory(List<BalanceHistory> balanceHistory) {
        this.balanceHistory = balanceHistory;
    }

    public List<BalanceHistory> getBalanceHistory() {
        return balanceHistory;
    }

    
}

