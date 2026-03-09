package com.sportsbook.nba.games.controller;

import com.sportsbook.nba.games.dto.OddsGameDto;
import com.sportsbook.nba.games.service.OddsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/odds")
public class OddsController {

    private final OddsService oddsService;

    // constructor injection for the service
    public OddsController(OddsService oddsService){
        this.oddsService = oddsService;
    }

    // endpoint: /api/odds/by-date?date=YYYY-MM-DD
    // allows the frontend to request odds for a specific day
    @GetMapping("/by-date")
    public List<OddsGameDto> oddsByDate(@RequestParam LocalDate date) {
        return oddsService.getOddsByDate(date);
    }
}
