package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "users") 
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;

    private String name;

    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL) 
    private List<CreditCard> creditCards;

    // Construct a user
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
