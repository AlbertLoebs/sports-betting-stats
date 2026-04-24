package com.sportsbook.nba.bet.dto;

import java.util.List;

public record BetHistoryDto(
        Long id,
        Long userId,
        Integer wagerCents,
        Integer combinedOdds,
        Integer potentialPayoutCents,
        String status,
        String createdAt,
        List<BetLegHistoryDto> legs
) {
}
