package com.sportsbook.nba.games.service;

import com.sportsbook.nba.games.dto.ParlayDto;
import com.sportsbook.nba.games.service.BalanceService;
import com.sportsbook.nba.games.dto.PlaceBetRequestDto;
import com.sportsbook.nba.games.dto.PlaceBetResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        // require one selection in order to place a bet
        if (request.selections() == null || request.selections().isEmpty()){
            throw new IllegalArgumentException("At least one selection is required");
        }

        // wager must be more than zero
        if (request.wager() == null || request.wager() <= 0){
            throw new IllegalArgumentException("Wager must be greater than zero");
        }

        validateSelections(request.selections());

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

        double combinedDecimalOdds = calculateCombinedDecimalOdds(request.selections());
        BigDecimal potentialPayout = wager.multiply(BigDecimal.valueOf(combinedDecimalOdds))
                .setScale(2, RoundingMode.HALF_UP);

        int combinedAmericanOdds = decimalToAmerican(combinedDecimalOdds);

        return new PlaceBetResponseDto(
                "Bet placed successfully.",
                request.selections(),
                wager.doubleValue(),
                combinedAmericanOdds,
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

    private void validateSelections(List<ParlayDto> selections){
        // set to avoid dups
        Set<String> gameIds = new HashSet<>();

        for (ParlayDto selection : selections){
            if (selection.gameId() == null || selection.gameId().isBlank()) {
                throw new IllegalArgumentException("Each selection must include a game ID.");
            }

            if (selection.teamName() == null || selection.teamName().isBlank()) {
                throw new IllegalArgumentException("Each selection must include a team name.");
            }

            if (selection.odds() == null) {
                throw new IllegalArgumentException("Each selection must include odds.");
            }

            if (!gameIds.add(selection.gameId())) {
                throw new IllegalArgumentException("Only one selection per game is allowed in a parlay.");
            }
        }

    }

    private double calculateCombinedDecimalOdds(List<ParlayDto> selections) {
        double combined = 1.0;

        for (ParlayDto selection : selections) {
            combined *= americanToDecimal(selection.odds());
        }

        return combined;
    }

    private double americanToDecimal(int americanOdds) {
        if (americanOdds > 0) {
            return 1.0 + (americanOdds / 100.0);
        } else {
            return 1.0 + (100.0 / Math.abs(americanOdds));
        }
    }

    private int decimalToAmerican(double decimalOdds) {
        if (decimalOdds >= 2.0) {
            return (int) Math.round((decimalOdds - 1.0) * 100);
        } else {
            return (int) Math.round(-100 / (decimalOdds - 1.0));
        }
    }

}