package com.sportsbook.nba.games.service;

import com.sportsbook.nba.games.dto.MoneyLineDto;
import com.sportsbook.nba.games.dto.OddsGameDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OddsService {

    // Used to make HTTP requests to the Odds API
    private final RestTemplate restTemplate = new RestTemplate();

    // my api key is loaded from application.properties
    @Value("${odds.api.key}")
    private String apiKey;

    // base api url loaded from app.prop
    @Value("${odds.api.base-url}")
    private String baseUrl;

    // stores prev fetched odds to avoid hitting api everytime
    private List<OddsGameDto> cachedOdds = new ArrayList<>();

    // stores the time of the last api fetch
    private long lastFetchTime = 0;

    // cache duration : 10 min in milliseconds
    private static final long CACHE_DURATION_MS = 10 * 60 * 1000;

    // returns todays odds
    // if cached data is still fresh, return it instead of calling api again

    public List<OddsGameDto> getTodaysOdds() {
        long now = System.currentTimeMillis();

        // return cached odds if they are still within the cache duration
        if (now - lastFetchTime < CACHE_DURATION_MS && !cachedOdds.isEmpty()) {
            return  cachedOdds;
        }

        // otherwise fetch fresh odds from api
        List<OddsGameDto> freshOdds = fetchOddsFromApi();

        // update cache
        cachedOdds = freshOdds;
        lastFetchTime = now;

        return freshOdds;
    }


    // calls the odds api and converts to oddsd to objects, using fanduel
    public List<OddsGameDto> fetchOddsFromApi() {

        // build the request url
        String url = baseUrl
                + "/sports/basketball_nba/odds"
                + "?regions=us"
                + "&markets=h2h"
                + "&oddsFormat=american"
                + "&apiKey=" + apiKey;

        // send get request to the odds api
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

        // the api response body is a list of game objects
        List<Map<String, Object>> games = response.getBody();

        // final list that will be returned to the controller
        List<OddsGameDto> result = new ArrayList<>();

        if (games == null){
            return result;
        }

        // loop thru each game that is returned by the api
        for (Map<String,Object> game : games){

            // extract game info
            String gameId = (String) game.get("gameid");
            String commenceTime = (String) game.get("commence_time");
            String homeTeam = (String) game.get("home_team");
            String awayTeam = (String) game.get("away_team");

            // these hold fanduel moneyline
            Integer homePrice = null;
            Integer awayPrice = null;

            // get list of all bookmakers
            List<Map<String, Object>> bookMakers =
                    (List<Map<String, Object>>) game.get("bookmakers");

            if (bookMakers != null){

                // just look for fanduel
                for (Map<String, Object> bookMaker : bookMakers) {
                    String bookMakerKey = (String) bookMaker.get("key");

                    // skip anything non fanduel
                    if (!"fanduel".equals(bookMakerKey)){
                        continue;
                    }

                    // get fanduels markets for this game
                    List<Map<String, Object>> markets =
                            (List<Map<String, Object>>) bookMaker.get("markets");

                    if (markets == null){
                        continue;
                    }

                    // find h2h market which is moneyline
                    for (Map<String, Object> market : markets) {
                        String marketKey = (String) market.get("key");

                        // only use h2h odds right now
                        if (!"h2h".equals(marketKey)){
                            continue;
                        }

                        // outcomes contain the prices for each team
                        List<Map<String, Object>> outcomes =
                                (List<Map<String, Object>>) market.get("outcomes");

                        if (outcomes == null){
                            continue;
                        }

                        // loop thru the two outcomes and match them to home/away
                        for (Map<String, Object> outcome : outcomes) {
                            String name = (String) outcome.get("name");

                            // convert price to integer
                            Number priceNumber = (Number) outcome.get("price");
                            Integer price = priceNumber != null ? priceNumber.intValue() : null;

                            // match the correct price to the correct team
                            if (homeTeam.equals(name)){
                                homePrice = price;
                            } else if (awayTeam.equals(name)){
                                awayPrice = price;
                            }
                        }
                    }
                }
            }

            // build nested moneyline dto
            MoneyLineDto moneyline = new MoneyLineDto(homePrice, awayPrice);

            // build final game dto
            OddsGameDto oddsGame = new OddsGameDto(
                    gameId,
                    commenceTime,
                    homeTeam,
                    awayTeam,
                    moneyline
            );

            // add this to the return list
            result.add(oddsGame);
        }
        return result;
    }

}
