package com.sportsbook.nba.games.controller;

import com.sportsbook.nba.games.dto.GameSummaryDto;
import com.sportsbook.nba.games.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    @GetMapping("/api/games/today")
    public List<GameSummaryDto> todaysGames(){
        return gameService.getTodaysGames();
    }

}
