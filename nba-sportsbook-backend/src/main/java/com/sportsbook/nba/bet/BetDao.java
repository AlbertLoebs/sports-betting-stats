package com.sportsbook.nba.bet;

import com.sportsbook.nba.bet.dto.BetHistoryDto;
import com.sportsbook.nba.bet.dto.BetLegHistoryDto;
import com.sportsbook.nba.model.Bet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BetDao {

    private final JdbcTemplate jdbc;

    public BetDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Long insertBet(Bet bet){
        String sql = " INSERT INTO bets (wager_cents, combined_odds, potential_payout_cents, status) VALUES (?, ?, ?, ?)";

        jdbc.update(sql, bet.getWagerCents(), bet.getCombinedOdds(), bet.getPotentialPayoutCents(), bet.getStatus());


        // This returns the ID of the last row inserted
        // (the bet we just created)
        return jdbc.queryForObject(
                "SELECT last_insert_rowid()", // SQL query
                Long.class                   // expected return type
        );
    }

    public void insertBetLeg(Long betId, String gameId, String team, int odds) {
        String sql = "INSERT INTO bet_legs (bet_id, game_id, team, odds, status) VALUES (?, ?, ?, ?, ?)";

        jdbc.update(sql, betId, gameId, team, odds, "OPEN");
    }

    public List<BetHistoryDto> getBetHistory(){
        String sql = """
                SELECT id, wager_cents, combined_odds, potential_payout_cents, status, created_at
                FROM bets
                ORDER BY created_at DESC, id DESC
        """;

        return jdbc.query(sql, (rs, rowNum) -> {
            Long betId = rs.getLong("id");

            return new BetHistoryDto(
                    betId,
                    rs.getInt("wager_cents"),
                    rs.getInt("combined_odds"),
                    rs.getInt("potential_payout_cents"),
                    rs.getString("status"),
                    rs.getString("created_at"),
                    getLegsForBet(betId)
            );
        });

    }

    public List<BetLegHistoryDto> getLegsForBet(Long betId) {
        String sql = """
                SELECT id, game_id, team, odds, status
                FROM bet_legs
                WHERE bet_id = ?
                ORDER BY id ASC
                """;

        return jdbc.query(sql, (rs, rowNum) -> new BetLegHistoryDto(
                rs.getLong("id"),
                rs.getString("game_id"),
                rs.getString("team"),
                rs.getInt("odds"),
                rs.getString("status")
        ), betId);
    }
}
