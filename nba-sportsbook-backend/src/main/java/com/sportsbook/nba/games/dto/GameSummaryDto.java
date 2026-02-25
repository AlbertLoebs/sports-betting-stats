package com.sportsbook.nba.games.dto;

public record GameSummaryDto(
        String gameId,
        String startTime,
        String status,
        TeamSummaryDto homeTeam,
        TeamSummaryDto awayTeam,
        Integer quarter,
        String clock
){}

