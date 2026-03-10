package com.sportsbook.nba.games.controller;

import com.sportsbook.nba.games.dto.AmountRequestDto;
import com.sportsbook.nba.games.dto.BalanceResponseDto;
import com.sportsbook.nba.games.service.BalanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance")
@CrossOrigin(origins = "http://localhost:5173")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping
    public BalanceResponseDto getBalance() {
        return balanceService.getBalance();
    }

    @PostMapping("/deposit")
    public BalanceResponseDto deposit(@RequestBody AmountRequestDto request) {
        return balanceService.deposit(request.amount());
    }

    @PostMapping("/withdraw")
    public BalanceResponseDto withdraw(@RequestBody AmountRequestDto request) {
        return balanceService.withdraw(request.amount());
    }
}
