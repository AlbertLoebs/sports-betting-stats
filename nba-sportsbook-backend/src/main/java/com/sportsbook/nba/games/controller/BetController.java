package com.sportsbook.nba.games.controller;

import com.sportsbook.nba.bet.dto.BetHistoryDto;
import com.sportsbook.nba.games.dto.PlaceBetRequestDto;
import com.sportsbook.nba.games.dto.PlaceBetResponseDto;
import com.sportsbook.nba.games.service.BetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bets")
@CrossOrigin(origins = "http://localhost:5173")
public class BetController {

    private final BetService betService;

    public BetController(BetService betService){
        this.betService = betService;
    }

    // endpoint handles bets /api/bets/place
    @PostMapping("place")
    public ResponseEntity<?> placeBet(@RequestBody PlaceBetRequestDto request){

        try {
            // call service to process bet
            PlaceBetResponseDto response = betService.placeBet(request);

            // return success (http 2000)
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e){
            // if validation fails return http 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public List<BetHistoryDto> getHistory(){
        return betService.getBetHistory();
    }

}
