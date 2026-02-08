package com.sportsbook.nba.games.dto;

public record TeamSummaryDto (
        String abbreviation,
        String displayName,
        Integer score
){}
