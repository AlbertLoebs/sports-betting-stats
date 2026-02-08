package com.sportsbook.nba.games.service;

import com.sportsbook.nba.games.dto.GameSummaryDto;
import com.sportsbook.nba.games.dto.TeamSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    public List<GameSummaryDto> getTodaysGames(){
        return List.of(
                new GameSummaryDto(
                        "Test 1",
                        "2026-02-08T03:00:00Z",
                        "Scheduled",
                        new TeamSummaryDto("Lal", "Lakers", null),
                        new TeamSummaryDto("GSW", "Warriors", null)
                    )
            );
    }

}
