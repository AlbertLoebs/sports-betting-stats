package com.sportsbook.nba.auth.dto;

// request body frontend sends to backend when registering new user

public record RegisterRequestDto (
        String username,
        String password
) {}
