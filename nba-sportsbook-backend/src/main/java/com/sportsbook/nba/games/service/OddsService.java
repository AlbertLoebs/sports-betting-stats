package com.sportsbook.nba.games.service;

import com.sportsbook.nba.games.dto.MoneyLineDto;
import com.sportsbook.nba.games.dto.OddsGameDto;
import com.sportsbook.nba.games.dto.SpreadDto;
import com.sportsbook.nba.games.dto.TotalDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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

    // returns requested date odds
    // first uses the cached full odds list if it is still fresh
    // then filters that list down to the selected date
    // returns odds only for the requested date
    // first uses the cached full odds list if it is still fresh
    // then filters that list down to the selected date
    public List<OddsGameDto> getOddsByDate(LocalDate date) {
        long now = System.currentTimeMillis();

        // if cache is expired or empty, fetch fresh odds first
        if (now - lastFetchTime >= CACHE_DURATION_MS || cachedOdds.isEmpty()) {
            List<OddsGameDto> freshOdds = fetchOddsFromApi();
            cachedOdds = freshOdds;
            lastFetchTime = now;
        }

        // now filter the cached odds list so only games on the requested date are returned
        List<OddsGameDto> filteredOdds = new ArrayList<>();

        for (OddsGameDto oddsGame : cachedOdds) {
            try {
                // commenceTime comes back as an ISO datetime string
                // example: 2026-03-10T23:30:00Z
                OffsetDateTime gameTime = OffsetDateTime.parse(oddsGame.commenceTime());

                // convert UTC odds time into Pacific time before comparing dates
                LocalDate gameDate = gameTime
                        .atZoneSameInstant(ZoneId.of("America/Los_Angeles"))
                        .toLocalDate();

                // only keep odds that match the requested date
                if (gameDate.equals(date)) {
                    filteredOdds.add(oddsGame);
                }
            } catch (Exception e) {
                // if a date fails to parse, skip that game safely
            }
        }

        return filteredOdds;
    }


    // calls the odds api and converts to oddsd to objects, using fanduel
    public List<OddsGameDto> fetchOddsFromApi() {

        // build the request url
        String url = baseUrl
                + "/sports/basketball_nba/odds"
                + "?regions=us"
                + "&markets=h2h,spreads,totals"
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
            String gameId = (String) game.get("id");
            String commenceTime = (String) game.get("commence_time");
            String homeTeam = (String) game.get("home_team");
            String awayTeam = (String) game.get("away_team");

            // these hold fanduel moneyline
            Integer homePrice = null;
            Integer awayPrice = null;

            // spread variables
            Double homePoint = null;
            Double awayPoint = null;
            Integer homeSpreadPrice = null;
            Integer awaySpreadPrice = null;

            // total variables
            Double totalLine = null;
            Integer overPrice = null;
            Integer underPrice = null;

            // get list of all bookmakers
            List<Map<String, Object>> bookMakers =
                    (List<Map<String, Object>>) game.get("bookmakers");

            if (bookMakers != null){

                // just look for fanduel
                for (Map<String, Object> bookMaker : bookMakers) {
                    String bookMakerKey = (String) bookMaker.get("key");

                    // skip anything non fanduel
                    if (!"fanduel".equals(bookMakerKey)) {
                        continue;
                    }

                    // get fanduels markets for this game
                    List<Map<String, Object>> markets =
                            (List<Map<String, Object>>) bookMaker.get("markets");

                    if (markets == null) {
                        continue;
                    }

                    // find h2h market which is moneyline
                    for (Map<String, Object> market : markets) {
                        String marketKey = (String) market.get("key");

                        // outcomes contain the prices for each team
                        List<Map<String, Object>> outcomes =
                                (List<Map<String, Object>>) market.get("outcomes");

                        if (outcomes == null) {
                            continue;
                        }

                        if ("h2h".equals(marketKey)) {
                            // loop thru the two outcomes and match them to home/away
                            for (Map<String, Object> outcome : outcomes) {
                                String name = (String) outcome.get("name");

                                // convert price to integer
                                Number priceNumber = (Number) outcome.get("price");
                                Integer price = priceNumber != null ? priceNumber.intValue() : null;

                                // match the correct price to the correct team
                                if (homeTeam.equals(name)) {
                                    homePrice = price;
                                } else if (awayTeam.equals(name)) {
                                    awayPrice = price;
                                }
                            }
                        }

                        if ("spreads".equals(marketKey)) {
                            for (Map<String, Object> outcome : outcomes) {
                                String name = (String) outcome.get("name");

                                Number pointNumber = (Number) outcome.get("point");
                                Double point = pointNumber != null ? pointNumber.doubleValue() : null;

                                Number priceNumber = (Number) outcome.get("price");
                                Integer price = priceNumber != null ? priceNumber.intValue() : null;

                                if (homeTeam.equals(name)) {
                                    homePoint = point;
                                    homeSpreadPrice = price;
                                } else if (awayTeam.equals(name)) {
                                    awayPoint = point;
                                    awaySpreadPrice = price;
                                }
                            }
                        }

                        if ("totals".equals(marketKey)) {
                            for (Map<String, Object> outcome : outcomes) {
                                String name = (String) outcome.get("name");

                                Number pointNumber = (Number) outcome.get("point");
                                Double point = pointNumber != null ? pointNumber.doubleValue() : null;

                                Number priceNumber = (Number) outcome.get("price");
                                Integer price = priceNumber != null ? priceNumber.intValue() : null;

                                if ("Over".equalsIgnoreCase(name)) {
                                    totalLine = point;
                                    overPrice = price;
                                } else if ("Under".equalsIgnoreCase(name)) {
                                    totalLine = point;
                                    underPrice = price;
                                }
                            }
                        }
                    }
                    break;
                }
            }

            // build nested moneyline dto
            MoneyLineDto moneyline = new MoneyLineDto(homePrice, awayPrice);

                SpreadDto spread = new SpreadDto(
                        homePoint,
                        homeSpreadPrice,
                        awayPoint,
                        awaySpreadPrice
                );

                TotalDto total = new TotalDto(
                        totalLine,
                        overPrice,
                        underPrice
                );


            // build final game dto
            OddsGameDto oddsGame = new OddsGameDto(
                    gameId,
                    commenceTime,
                    homeTeam,
                    awayTeam,
                    moneyline,
                    spread,
                    total
            );

            // add this to the return list
            result.add(oddsGame);
        }
        return result;
    }

}
