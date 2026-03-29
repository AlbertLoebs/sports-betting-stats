package com.sportsbook.nba.games.service;

import com.sportsbook.nba.games.service.BalanceService;
import com.sportsbook.nba.games.dto.PlaceBetRequestDto;
import com.sportsbook.nba.games.dto.PlaceBetResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class BetService {

    // this lets the bet service use the existing balance service
    // so we can check and update the user's balance
    private final BalanceService balanceService;

    public BetService(BalanceService balanceService){
        this.balanceService = balanceService;
    }

    // main method to place a bet
    public PlaceBetResponseDto placeBet(PlaceBetRequestDto request){
        // validate the game id
        if (request.gameId() == null || request.gameId().isBlank()){
            throw new IllegalArgumentException("Game ID is required.");
        }

        // validate the team
        if (request.teamName() == null || request.teamName().isBlank()){
            throw new IllegalArgumentException("Team name is required.");
        }

        // validate odds
        if (request.odds() == null){
            throw new IllegalArgumentException("Odds are required");
        }

        // validate wager
        if (request.wager() == null) {
            throw new IllegalArgumentException("wager is required");
        }

        // wager must be more than zero
        if (request.wager() <= 0){
            throw new IllegalArgumentException("Wager must be greater than zero");
        }

        // convert wager from Double to BigDecimal for safer money math
        BigDecimal wager = BigDecimal.valueOf(request.wager());

        BigDecimal currentBalance = balanceService.getBalance();

        // check if user has enough money
        if (wager.compareTo(currentBalance) > 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        // substract the wager
        BigDecimal newBalance = currentBalance.subtract(wager);

        // set the updated balance
        balanceService.setBalance(newBalance);

        // calculate how much the user would get back if the bet wins
        BigDecimal potentialPayout = calculatePayout(wager, request.odds());

        // build and return the response dto
        return new PlaceBetResponseDto(
                "Bet placed successfully.",
                request.gameId(),
                request.teamName(),
                request.odds(),
                wager.doubleValue(),
                potentialPayout.doubleValue(),
                newBalance.doubleValue()
        );
    }

    // helper method to calculate total payout from american odds
    private BigDecimal calculatePayout(BigDecimal wager, int odds) {
        BigDecimal payout;

        // positive odds example: +150
        // payout = wager + (wager * odds / 100)
        if (odds > 0) {
            payout = wager.add(
                    wager.multiply(BigDecimal.valueOf(odds))
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
            );
        }

        // negative odds example: -120
        // payout = wager + (wager * 100 / abs(odds))
        else {
            payout = wager.add(
                    wager.multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(Math.abs(odds)), 2, RoundingMode.HALF_UP)
            );
        }

        // round to 2 decimal places for money
        return payout.setScale(2, RoundingMode.HALF_UP);
    }
}