package com.sportsbook.nba.games.controller;

import com.sportsbook.nba.games.dto.OddsGameDto;
import com.sportsbook.nba.games.service.OddsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/odds")
public class OddsController {

    private final OddsService oddsService;

    // constructor injection for the service
    public OddsController(OddsService oddsService){
        this.oddsService = oddsService;
    }

    // returns todays nba moneyline odds
    // enpoint /api/odds/today
    @GetMapping("/today")
    public List<OddsGameDto> getTodayOdds(){
            return oddsService.getTodaysOdds();
        }
}
