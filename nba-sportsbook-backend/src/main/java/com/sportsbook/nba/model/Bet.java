package com.sportsbook.nba.model;

public class Bet {
    private Long id;
    private String gameId;
    private Integer wagerCents;
    private Integer combinedOdds;
    private String status;
    private Integer potentialPayoutCents;

    public Bet(){}

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public String getGameId(){return gameId;}
    public void setGameId(String gameId){this.gameId = gameId;}

    public Integer getWagerCents(){return wagerCents;}
    public void setWagerCents(Integer wagerCents){this.wagerCents = wagerCents;}

    public Integer getCombinedOdds(){return combinedOdds;}
    public void setCombinedOdds(Integer combinedOdds){this.combinedOdds = combinedOdds;}

    public String getStatus(){return status;}
    public void setStatus(String status){this.status = status;}

    public Integer getPotentialPayoutCents(){return potentialPayoutCents;}
    public void setPotentialPayoutCents(Integer potentialPayoutCents) {
        this.potentialPayoutCents = potentialPayoutCents;
    }
}
