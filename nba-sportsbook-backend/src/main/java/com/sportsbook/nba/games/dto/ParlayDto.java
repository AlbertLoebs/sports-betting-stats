package com.sportsbook.nba.games.dto;

public record ParlayDto(
        String gameId,
        String selection,
        String betType,
        Double line,
        Integer odds
) {}
