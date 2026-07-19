package com.eiou.mybatis.practice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class DemoUser {
    private Long id;
    private String username;
    private String status;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public DemoUser() {
    }

    public DemoUser(String username, String status, BigDecimal balance, LocalDateTime createdAt) {
        this.username = username;
        this.status = status;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DemoUser{"
                + "id=" + id
                + ", username='" + username + '\''
                + ", status='" + status + '\''
                + ", balance=" + balance
                + ", createdAt=" + createdAt
                + '}';
    }
}
