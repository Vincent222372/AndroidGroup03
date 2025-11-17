package com.example.a7436.model;

import java.time.LocalDate;

public class Expense {
    private String description;
    private double amount;
    private LocalDate date;


    public Expense(String description) {
        this.description = description;
    }
}
