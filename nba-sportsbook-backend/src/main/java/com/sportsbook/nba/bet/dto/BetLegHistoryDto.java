package com.sportsbook.nba.bet.dto;

public record BetLegHistoryDto(
        Long id,
        String gameId,
        String team,
        Integer odds,
        String status
) {
}
