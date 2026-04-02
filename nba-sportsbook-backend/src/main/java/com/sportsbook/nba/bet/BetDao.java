package com.sportsbook.nba.bet;

import com.sportsbook.nba.model.Bet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

}
