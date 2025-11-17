package com.example.a7436.model;

public class Expense {
    private int expenseId;
    private int userId;
    private int categoryId;
    private String description;
    private String date;
    private double amount;
    private boolean isRecurring;
    private String startDate;
    private String endDate;
    private String frequency;

    // Constructor for simple, non-recurring expense
    public Expense(int userId, int categoryId, String description, String date, double amount) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.isRecurring = false;
    }

    // Full constructor
    public Expense(int expenseId, int userId, int categoryId, String description, String date, double amount, boolean isRecurring, String startDate, String endDate, String frequency) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.isRecurring = isRecurring;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
    }

    // Getters and Setters
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
