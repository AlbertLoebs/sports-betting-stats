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
                        new TeamSummaryDto("Lal", "Lakers", 101),
                        new TeamSummaryDto("GSW", "Warriors", 100)
                    ),
                new GameSummaryDto(
                        "Test 2",
                        "2026-02-08T03:00:00Z",
                        "Scheduled",
                        new TeamSummaryDto("Lal", "Lakers", 101),
                        new TeamSummaryDto("GSW", "Warriors", 1200)
                ),
                new GameSummaryDto(
                        "Test 3",
                        "2026-02-08T03:00:00Z",
                        "Scheduled",
                        new TeamSummaryDto("Lal", "Lakers", 1021),
                        new TeamSummaryDto("GSW", "Warriors", 100)
                )
            );
    }

}
