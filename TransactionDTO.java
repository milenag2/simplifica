package com.simplifica.simplificajava.dto;

import java.time.LocalDate;

public class TransactionDTO {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String description;
    private Double amount;
    private String type;
    private LocalDate date;
    private Boolean isRecurring;
    private String recurringFrequency;
    private LocalDate recurringEndDate;
    private String categoryName;

    public TransactionDTO() {
    }

    public TransactionDTO(Long id, Long userId, Long categoryId, String description, Double amount, String type, LocalDate date, Boolean isRecurring, String recurringFrequency, LocalDate recurringEndDate, String categoryName) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.isRecurring = isRecurring;
        this.recurringFrequency = recurringFrequency;
        this.recurringEndDate = recurringEndDate;
        this.categoryName = categoryName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurringFrequency() {
        return recurringFrequency;
    }

    public void setRecurringFrequency(String recurringFrequency) {
        this.recurringFrequency = recurringFrequency;
    }

    public LocalDate getRecurringEndDate() {
        return recurringEndDate;
    }

    public void setRecurringEndDate(LocalDate recurringEndDate) {
        this.recurringEndDate = recurringEndDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

