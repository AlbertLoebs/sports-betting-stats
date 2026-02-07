package com.sportsbook.nba.games.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @GetMapping("/api/games/today")
    public String getGamesToday(){
        return "Hello this is a endpoint test";
    }

}
