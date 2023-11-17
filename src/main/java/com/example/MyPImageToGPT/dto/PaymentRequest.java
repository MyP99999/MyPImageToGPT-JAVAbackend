package com.example.MyPImageToGPT.dto;

public class PaymentRequest {
    private Long amount;
    private Integer userId; // User ID for whom the payment is made

    // Constructor, getters, and setters

    public PaymentRequest() {
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
