package com.sportsbook.nba.model;

public class BetLeg {
    private Long id;
    private String betId;
    private String gameId;
    private String team;
    private Integer odds;
    private String status;

    public BetLeg() {};

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public String getBetId(){return betId;}
    public void setBetId(String betId){this.betId = betId;}

    public String getGameId(){return gameId;}
    public void setGameId(String gameId){this.gameId = gameId;}

    public String getTeam(){return team;}
    public void setTeam(String team){this.team = team;}

    public Integer getOdds(){return odds;}
    public void setOdds(Integer odds){this.odds = odds;}

    public String getStatus(){return status;}
    public void setStatus(String status){this.status = status;}
}
