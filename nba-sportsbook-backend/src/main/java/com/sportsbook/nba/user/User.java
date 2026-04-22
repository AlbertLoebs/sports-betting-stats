package com.sportsbook.nba.user;

// represents a row in the users table

public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private Integer balanceCents;

    // empty constructor
    public User(){}

    // full contructor
    public User(Long id, String username, String passwordHash, Integer balanceCents){
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.balanceCents = balanceCents;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Integer getBalanceCents() {
        return balanceCents;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setBalanceCents(Integer balanceCents) {
        this.balanceCents = balanceCents;
    }

}
