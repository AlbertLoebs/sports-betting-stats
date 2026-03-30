package com.sportsbook.nba.games.dto;

import java.util.List;

public record PlaceBetResponseDto(
        String message,
        List<ParlayDto> selections,
        Double wager,
        Integer combinedAmericanOdds,
        Double potentialPayout,
        Double newBalance
) {}
