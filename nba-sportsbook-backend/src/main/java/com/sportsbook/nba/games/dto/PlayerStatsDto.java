package com.sportsbook.nba.games.dto;

public record PlayerStatsDto(
        String name,
        String headshotUrl,
        String position,
        String minutes,
        String points,
        String rebounds,
        String assists,
        String steals,
        String blocks,
        String fieldGoals,
        String threePointers,
        String freeThrows
) {
}