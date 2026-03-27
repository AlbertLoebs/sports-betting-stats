package com.sportsbook.nba.games.dto;

public record PlaceBetResponseDto(
        String message,
        String gameId,
        String teamName,
        Integer odds,
        Double wager,
        Double potentialPayout,
        Double newBalance
) {
}
