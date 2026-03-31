package com.sportsbook.nba.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    // JdbcTemplate is provided by spring and makes sql run easy
    private final JdbcTemplate jdbc;

    public UserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // get current balance for a user in cents
    public long getBalanceCents(String username){
        String sql = "SELECT balance_cents FROM users WHERE username = ?";

        // queryForObject runs the query and returns the value
        return jdbc.queryForObject(sql, Long.class, username);
    }

    // increase balance
    public void deposit(String username, long amount){
        String sql = "UPDATE users SET balance_cents = balance_cents + ? WHERE username = ?";

        jdbc.update(sql, amount, username);
    }

    // withdraw
    public void withdraw(String username, long amount){
        String sql = "UPDATE users SET balance_cents = balance_cents - ? WHERE username = ?";

        jdbc.update(sql, amount, username);
    }
}
