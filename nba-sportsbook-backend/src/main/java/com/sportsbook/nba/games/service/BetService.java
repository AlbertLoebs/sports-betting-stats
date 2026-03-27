package com.sportsbook.nba.games.service;

import com.sportsbook.nba.games.dto.PlaceBetRequestDto;
import com.sportsbook.nba.games.dto.PlaceBetResponseDto;
import org.springframework.stereotype.Service;

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
    }

}
