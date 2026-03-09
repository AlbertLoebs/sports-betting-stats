package com.sportsbook.nba.games.controller;

import com.sportsbook.nba.games.dto.GameSummaryDto;
import com.sportsbook.nba.games.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class GameController {

    // ref to service with logic
    private final GameService gameService;

    // constructor injection
    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    // endpoint for todays games
    @GetMapping("/api/games/today")
    public List<GameSummaryDto> todaysGames(){
        return gameService.getGamesByDate(LocalDate.now());
    }

    // endpoint that lets the frontend request games for a cetain date
    // ex) /api/games/by-date?date=2026-03-09
    // @ReqParam - take date value from the URL query string and put it into this variable
    @GetMapping("/api/games/by-date")
    public List<GameSummaryDto> gamesByDate(@RequestParam LocalDate date){
        return gameService.getGamesByDate(date);
    }

}
