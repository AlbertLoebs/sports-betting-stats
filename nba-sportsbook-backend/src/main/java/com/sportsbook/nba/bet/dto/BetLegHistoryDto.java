package com.sportsbook.nba.bet.dto;

public record BetLegHistoryDto(
        Long id,
        String gameId,
        String selection,
        String betType,
        Double line,
        Integer odds,
        String status
) {
}