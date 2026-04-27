package com.sportsbook.nba.games.dto;

import java.util.List;

public record TeamPlayerStatsDto(
        String teamName,
        String abbreviation,
        List<PlayerStatsDto> players
) {}