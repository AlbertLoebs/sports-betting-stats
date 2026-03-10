package com.sportsbook.nba.games.service;

import com.sportsbook.nba.games.dto.BalanceResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {

    // temp in-memory balance, will reset on backend restart
    // will add db later
    private BigDecimal balance = new BigDecimal("100");

    public BalanceResponseDto deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
        return new BalanceResponseDto(balance);
    }

    public BalanceResponseDto withdraw(BigDecimal amount) {
        validateAmount(amount);

        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        balance = balance.subtract(amount);
        return new BalanceResponseDto(balance);
    }

    public BalanceResponseDto getBalance() {
        return new BalanceResponseDto(balance);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }
}