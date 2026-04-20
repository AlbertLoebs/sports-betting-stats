package com.sportsbook.nba.games.dto;

public record SpreadDto(
    Double homePoint,
    Integer homePrice,
    Double awayPoint,
    Integer awayPrice
) {
}
