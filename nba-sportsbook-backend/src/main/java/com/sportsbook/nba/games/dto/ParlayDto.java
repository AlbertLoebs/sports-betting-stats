package com.sportsbook.nba.games.dto;

public record ParlayDto(
        String gameId,
        String teamName,
        Integer odds
) {}
