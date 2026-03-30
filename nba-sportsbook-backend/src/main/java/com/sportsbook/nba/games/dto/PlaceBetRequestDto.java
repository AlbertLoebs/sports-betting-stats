package com.sportsbook.nba.games.dto;

import java.util.List;

public record PlaceBetRequestDto(
        List<ParlayDto> selections,
        Double wager
) {
}
