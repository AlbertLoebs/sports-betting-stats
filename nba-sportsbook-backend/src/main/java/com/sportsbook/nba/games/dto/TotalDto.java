package com.sportsbook.nba.games.dto;

public record TotalDto(
        Double line,
        Integer overPrice,
        Integer underPrice
) {
}
