package com.sportsbook.nba.games.dto;

import java.math.BigDecimal;

// incoming request body for depo/withdraw
public record AmountRequestDto(BigDecimal amount) {

}
