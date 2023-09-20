package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "balance_history") 
public class BalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant date;

    private double balance;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;

    public BalanceHistory(Instant date, double balance) {
        this.date = date;
        this.balance = balance;
    }
}
