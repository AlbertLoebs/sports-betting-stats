package com.sportsbook.nba.auth.dto;

// response sent after login/register
// does not contain sensitive info
public record AuthResponseDto(
        Long id,
        String username
) {
}
