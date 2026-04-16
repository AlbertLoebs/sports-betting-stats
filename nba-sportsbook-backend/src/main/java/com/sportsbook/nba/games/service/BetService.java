package com.sportsbook.nba.games.service;

import com.sportsbook.nba.bet.BetDao;
import com.sportsbook.nba.bet.dto.BetHistoryDto;
import com.sportsbook.nba.bet.dto.BetLegHistoryDto;
import com.sportsbook.nba.games.dto.*;
import com.sportsbook.nba.games.service.BalanceService;
import com.sportsbook.nba.model.Bet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

    // lets this service look up actual NBA game results from ESPN data
    private final GameService gameService;

    public BetService(BalanceService balanceService, BetDao betDao, GameService gameService) {
        this.balanceService = balanceService;
        this.betDao = betDao;
        this.gameService = gameService;
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
        bet.setStatus("PENDING");                     // bet status

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


    // when bet requested grade all pending bets
    // then return new history
    @Transactional
    public List<BetHistoryDto> getBetHistory() {
        gradePendingBets();
        return betDao.getBetHistory();
    }

    // check all pending bets, grade each leg, update entire bet
    // payout if won
    @Transactional
    public void gradePendingBets() {
        List<BetHistoryDto> pendingBets = betDao.getPendingBets();

        for (BetHistoryDto bet : pendingBets) {
            boolean anyLost = false;
            boolean allWon = true;

            for (BetLegHistoryDto leg : bet.legs()) {
                String legResult = gradeLeg(leg);

                // update if leg status changed
                if (!legResult.equals(leg.status())) {
                    betDao.updateBetLegStatus(leg.id(), legResult);
                }

                if ("LOST".equals(legResult)) {
                    anyLost = true;
                }

                if (!"WON".equals(legResult)) {
                    allWon = false;
                }
            }

            // this part is AFTER the leg loop
            if (anyLost) {
                betDao.updateBetStatus(bet.id(), "LOST");
            } else if (allWon) {
                betDao.updateBetStatus(bet.id(), "WON");

                BigDecimal payoutDollars = BigDecimal.valueOf(bet.potentialPayoutCents())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                balanceService.deposit(payoutDollars);
            }
        }
    }

    // grades one indivdual leg based on game result
    private String gradeLeg(BetLegHistoryDto leg) {
        GameSummaryDto game = findGameById(leg.gameId());

        // if cant find game keep pending
        if (game == null){
            return "PENDING";
        }

        // not over
        if (!isFinal(game)){
            return "PENDING";
        }

        String winningTeam = getWinningTeam(game);

        // if seleected team won, leg won
        if ((winningTeam != null && winningTeam.equalsIgnoreCase(leg.team()))){
            return "WON";
        }

        // otherwise lost
        return "LOST";
    }

    // find the game by id, check by 7 day window
    private GameSummaryDto findGameById(String gameId) {
        LocalDate today =  LocalDate.now();

        for (int i = -30; i <= 7; i++) {
            LocalDate date = today.plusDays(i);
            List<GameSummaryDto> games = gameService.getGamesByDate(date);

            for (GameSummaryDto game : games) {
                if (game.gameId().equals(gameId)) {
                    return game;
                }
            }
        }
        return null;
    }

    // check if game is over
    private boolean isFinal(GameSummaryDto game) {
        return game.status() != null && game.status().equalsIgnoreCase("FINAL");
    }

    // return winning team display name
    private String getWinningTeam(GameSummaryDto game) {
        Integer homeScore = game.homeTeam().score();
        Integer awayScore = game.awayTeam().score();

        if (homeScore == null || awayScore == null) {
            return null;
        }

        if (homeScore > awayScore) {
            return game.homeTeam().displayName();
        } else if (awayScore > homeScore) {
            return game.awayTeam().displayName();
        }
        return null;
    }


}