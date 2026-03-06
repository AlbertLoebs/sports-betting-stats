package com.sportsbook.nba.games.dto;

public record OddsGameDto(
        String gameId,
        String commenceTime,
        String homeTeam,
        String awayTeam,
        MoneyLineDto moneyline
) {
}
