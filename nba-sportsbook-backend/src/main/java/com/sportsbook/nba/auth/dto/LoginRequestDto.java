package com.sportsbook.nba.auth.dto;

// request body for login
public record LoginRequestDto(
        String username,
        String password
) {
}
