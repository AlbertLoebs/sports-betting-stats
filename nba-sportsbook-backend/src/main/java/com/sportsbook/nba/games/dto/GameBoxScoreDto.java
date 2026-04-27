package com.sportsbook.nba.games.dto;

import java.util.List;

public record GameBoxScoreDto(
        String gameId,
        String status,
        String homeTeam,
        String awayTeam,
        List<TeamPlayerStatsDto> teams
) {
}