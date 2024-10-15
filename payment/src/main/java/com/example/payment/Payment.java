package com.example.payment;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

class Payment {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String name;
private String number;
private String email;
private String address;
private int billValue;
private int cardNumber;
private String cardHolder;
private String dateValue;
private String cvc;
}