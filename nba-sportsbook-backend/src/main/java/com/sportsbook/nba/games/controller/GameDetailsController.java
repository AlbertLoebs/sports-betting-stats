package com.sportsbook.nba.games.controller;

import com.sportsbook.nba.games.dto.GameBoxScoreDto;
import com.sportsbook.nba.games.service.GameDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:5173")
public class GameDetailsController {

    private final GameDetailsService gameDetailsService;

    public GameDetailsController(GameDetailsService gameDetailsService) {
        this.gameDetailsService = gameDetailsService;
    }

    @GetMapping("/{gameId}/boxscore")
    public GameBoxScoreDto getGameBoxScore(@PathVariable String gameId) {
        return gameDetailsService.getGameDetails(gameId);
    }
}