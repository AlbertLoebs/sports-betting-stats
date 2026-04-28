package com.sportsbook.nba.games.controller;

import com.sportsbook.nba.bet.dto.BetHistoryDto;
import com.sportsbook.nba.games.dto.PlaceBetRequestDto;
import com.sportsbook.nba.games.dto.PlaceBetResponseDto;
import com.sportsbook.nba.games.service.BetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bets")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BetController {

    private final BetService betService;

    public BetController(BetService betService){
        this.betService = betService;
    }

    // endpoint handles bets /api/bets/place
    @PostMapping("/place")
    public ResponseEntity<?> placeBet(@RequestBody PlaceBetRequestDto request, HttpSession session) {
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            PlaceBetResponseDto response =
                    betService.placeBet((Long) userIdObj, request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(HttpSession session) {
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Long userId = (Long) userIdObj;

        return ResponseEntity.ok(betService.getBetHistory(userId));
    }

}
