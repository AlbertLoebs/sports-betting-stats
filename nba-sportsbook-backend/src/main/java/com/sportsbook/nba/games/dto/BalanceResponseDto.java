package com.sportsbook.nba.games.dto;

import java.math.BigDecimal;

// response body returned to fronted
public record BalanceResponseDto(BigDecimal balance) {
}
