package com.sportsbook.nba.games.service;

import com.sportsbook.nba.bet.BetDao;
import com.sportsbook.nba.bet.dto.BetHistoryDto;
import com.sportsbook.nba.games.dto.BalanceResponseDto;
import com.sportsbook.nba.games.dto.ParlayDto;
import com.sportsbook.nba.games.service.BalanceService;
import com.sportsbook.nba.games.dto.PlaceBetRequestDto;
import com.sportsbook.nba.games.dto.PlaceBetResponseDto;
import com.sportsbook.nba.model.Bet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // lets service save bets to the db
    private final BetDao betDao;


    public BetService(BalanceService balanceService, BetDao betDao){
        this.balanceService = balanceService;
        this.betDao = betDao;
    }

    // main method to place a bet
    @Transactional
    public PlaceBetResponseDto placeBet(PlaceBetRequestDto request){
        // require at least one selection
        if (request.selections() == null || request.selections().isEmpty()){
            throw new IllegalArgumentException("At least one selection is required");
        }

        // wager must be > 0
        if (request.wager() == null || request.wager() <= 0){
            throw new IllegalArgumentException("Wager must be greater than zero");
        }

        // validate each selection (no duplicates, valid fields, etc.)
        validateSelections(request.selections());

        // request.wager() is a Double (dollars)
        // convert to BigDecimal for safe money math
        BigDecimal wagerDollars = BigDecimal.valueOf(request.wager());

        // convert dollars → cents (for DB storage)
        // example: 10.50 → 1050
        int wagerCents = wagerDollars
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        // get current balance
        BigDecimal currentBalance = balanceService.getBalance();

        // user has enough money
        if (wagerDollars.compareTo(currentBalance) > 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        // calculate combined decimal odds for parlay
        double combinedDecimal = calculateCombinedDecimalOdds(request.selections());

        // convert decimal → american odds
        int combinedOdds = decimalToAmerican(combinedDecimal);

        // calculate payout in cents
        int payoutCents = (int)(wagerCents * combinedDecimal);

        Bet bet = new Bet();
        bet.setWagerCents(wagerCents);             // total wager
        bet.setCombinedOdds(combinedOdds);         // parlay odds
        bet.setPotentialPayoutCents(payoutCents);  // possible payout
        bet.setStatus("OPEN");                     // bet status

        // insert into "bets" table
        // returns generated bet_id
        Long betId = betDao.insertBet(bet);

        // each selection becomes a row in bet_legs
        for (ParlayDto selection : request.selections()) {
            betDao.insertBetLeg(
                    betId,                    // links to main bet
                    selection.gameId(),       // game
                    selection.teamName(),     // team picked
                    selection.odds()          // odds for this leg
            );
        }

        // remove wager from user's balance
        BalanceResponseDto updated = balanceService.withdraw(wagerDollars);

        // get new balance after bet
        BigDecimal newBalance = updated.balance();

        // convert payout cents → dollars for frontend
        BigDecimal payoutDollars = BigDecimal.valueOf(payoutCents)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return new PlaceBetResponseDto(
                "Bet placed successfully.",
                request.selections(),
                wagerDollars.doubleValue(),
                combinedOdds,
                payoutDollars.doubleValue(),
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

    public List<BetHistoryDto> getBetHistory() {
        return betDao.getBetHistory();
    }

}