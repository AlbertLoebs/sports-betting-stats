package com.sportsbook.nba.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {

    // JdbcTemplate is provided by spring and makes sql run easy
    private final JdbcTemplate jdbc;

    public UserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // find a user by username, returns null if no user found
    public User findByUsername(String username) {
        String sql = """
                SELECT id, username, password_hash, balance_cents
                FROM users
                WHERE username = ?
                """;

        List<User> users = jdbc.query(sql, (rs, rowNum) ->
                        new User(rs.getLong("id"),
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                rs.getInt("balance_cents")
                        ),
                username
        );
        return users.isEmpty() ? null : users.get(0);
    }

    // find a user by id
    public User findById(Long id) {
        String sql = """
                SELECT id, username, password_hash, balance_cents
                FROM users
                WHERE id = ?
                """;

        List<User> users = jdbc.query(sql, (rs, rowNum) ->
                        new User(rs.getLong("id"),
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                rs.getInt("balance_cents")
                        ),
                id
        );
        return users.isEmpty() ? null : users.get(0);
    }

    // create a new user
    public void createUser(String username, String password_hash, Integer balance_cents){
        String sql = """
                INSERT INTO users(username, password_hash, balance_cents)
                VALUES (?, ?, ?)
        """;
        jdbc.update(sql, username, password_hash, balance_cents);
    }

    // get current balance for a user in cents
    public long getBalanceCents(Long userId){
        String sql = "SELECT balance_cents FROM users WHERE id = ?";

        // queryForObject runs the query and returns the value
        return jdbc.queryForObject(sql, Long.class, userId);
    }

    // increase balance
    public void deposit(Long userId, long amount){
        String sql = "UPDATE users SET balance_cents = balance_cents + ? WHERE id = ?";

        jdbc.update(sql, amount, userId);
    }

    // withdraw
    public void withdraw(Long userId, long amount){
        String sql = "UPDATE users SET balance_cents = balance_cents - ? WHERE id = ?";

        jdbc.update(sql, amount, userId);
    }

    public void updateBalanceById(Long userId, long newBalanceCents) {
        String sql = "UPDATE users SET balance_cents = ? WHERE id = ?";

        jdbc.update(sql, newBalanceCents, userId);
    }
}
