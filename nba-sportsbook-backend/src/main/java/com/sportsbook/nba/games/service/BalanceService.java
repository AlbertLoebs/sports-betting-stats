package com.sportsbook.nba.games.service;

import com.sportsbook.nba.games.dto.BalanceResponseDto;
import com.sportsbook.nba.user.UserDao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {


    // DAO used to talk to db
    private final UserDao userDao;

    public BalanceService(UserDao userDao){
        this.userDao = userDao;
    }

    // use a single demo for now
    //private static final String USERNAME = "Demo";

    // convert dollars to cents
    private long toCents(BigDecimal amount){
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }

    // convert cents to dollars
    private BigDecimal toDollars(Long cents){
        return BigDecimal.valueOf(cents).divide(BigDecimal.valueOf(100));
    }

    public BalanceResponseDto deposit(Long userId, BigDecimal amount) {
        validateAmount(amount);

        // convert input dollars to cents
        long cents = toCents(amount);

        // update the db
        userDao.deposit(userId, cents);

        // fetch updated balance from db
        long updated = userDao.getBalanceCents(userId);

        // convert back to dollars for response
        return new BalanceResponseDto(toDollars(updated));
    }

    public BalanceResponseDto withdraw(Long userId, BigDecimal amount) {
        validateAmount(amount);

        long cents = toCents(amount);

        // get current balacne from db
        long current = userDao.getBalanceCents(userId);

        if (cents > current) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // update db
        userDao.withdraw(userId, cents);
        long updated = userDao.getBalanceCents(userId);
        return new BalanceResponseDto(toDollars(updated));
    }

    public BigDecimal getBalance(Long userId) {
        // read balance from db
        long cents = userDao.getBalanceCents(userId);

        // convert to dollars for frontend
        return toDollars(cents);
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