package com.sportsbook.nba.games.dto;

public record PlaceBetRequestDto(
        String gameId,
        String teamName,
        Integer odds,
        Double wager
) {
}
