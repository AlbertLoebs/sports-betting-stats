package com.sportsbook.nba.games.controller;

import com.sportsbook.nba.games.dto.AmountRequestDto;
import com.sportsbook.nba.games.dto.BalanceResponseDto;
import com.sportsbook.nba.games.service.BalanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    // Gets the logged-in user's id from the session.
    private Long getLoggedInUserId(HttpSession session) {
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj == null) {
            throw new RuntimeException("Not logged in");
        }

        return (Long) userIdObj;
    }


    // Get balance for the currently logged-in user.
    @GetMapping
    public BalanceResponseDto getBalance(HttpSession session) {
        Long userId = getLoggedInUserId(session);

        return new BalanceResponseDto(balanceService.getBalance(userId));
    }


    // Deposit money into the currently logged-in user's account.
    @PostMapping("/deposit")
    public BalanceResponseDto deposit(@RequestBody AmountRequestDto request, HttpSession session) {
        Long userId = getLoggedInUserId(session);

        return balanceService.deposit(userId, request.amount());
    }


    // Withdraw money from the currently logged-in user's account.
    @PostMapping("/withdraw")
    public BalanceResponseDto withdraw(@RequestBody AmountRequestDto request, HttpSession session) {
        Long userId = getLoggedInUserId(session);

        return balanceService.withdraw(userId, request.amount());
    }
}