package com.sportsbook.nba.games.service;

import com.sportsbook.nba.games.dto.GameSummaryDto;
import com.sportsbook.nba.games.dto.TeamSummaryDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    // ESPN public scoreboard endpoint for nba games
    private static final String ESPN_URL =
            "https://site.api.espn.com/apis/site/v2/sports/basketball/nba/scoreboard";

    // Used to make HTTP requests to ESPN
    private final RestClient restClient;

    // Used to parse JSON into a structure, will use JsonNode tree
    private final ObjectMapper objectMapper;

    // Spring injects objectmapper automatically
    public GameService (ObjectMapper objectMapper){
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
    }

    public List<GameSummaryDto> getTodaysGames() {

        try {
            // Call ESPN API, get a raw JSON response as a string
            String json = restClient.get().uri(ESPN_URL).retrieve().body(String.class);

            // convert json string into a jsonNode tree structure
            // makes it easier to deal with json fields
            JsonNode root = objectMapper.readTree(json);

            // ESPN scoreboard json contains an events array holding info
            JsonNode events = root.path("events");

            List<GameSummaryDto> result = new ArrayList<>();

            // loop through each game
            for (JsonNode event : events) {
                // basic game info
                String gameId = event.path("id").asText();
                String startTime = event.path("date").asText();

                // Game status is nested inside status -> type -> description
                String status = event.path("status")
                        .path("type")
                        .path("description")
                        .asText();

                // Fallback if description is missing
                if (status == null || status.isBlank()) {
                    status = event.path("status")
                            .path("type")
                            .path("name")
                            .asText();
                }

                // each event has a competitions array
                // nba games have 1 competition object
                JsonNode competition = event.path("competitions").isArray()
                        && event.path("competitions").size() > 0
                        ? event.path("competitions").get(0)
                        : null;

                // if competition is missing, skip safely
                if (competition == null) continue;
                TeamSummaryDto home = null;
                TeamSummaryDto away = null;

                // inside competition there is competitors which contains home and away teams
                JsonNode competitors = competition.path("competitors");

                Integer quarter = parseNullableInt(competition.path("status").path("period").asText());
                String clock = blankToNull(competition.path("status").path("displayClock").asText());


                for (JsonNode comp : competitors) {
                    // determines if they are home or away
                    String homeAway = comp.path("homeAway").asText("");

                    // team info is nested inside team
                    JsonNode team = comp.path("team");

                    String abbr = team.path("abbreviation").asText();
                    String name = team.path("displayName").asText();

                    // ESPN stores score as a string have to convert
                    Integer score = parseNullableInt(comp.path("score").asText());

                    // build internal team DTO
                    TeamSummaryDto teamDTO =
                            new TeamSummaryDto(abbr, name, score);

                    // assign home away
                    if ("home".equalsIgnoreCase(homeAway)) {
                        home = teamDTO;
                    }

                    if ("away".equalsIgnoreCase(homeAway)) {
                        away = teamDTO;
                    }
                }
                    // if something is missing, skip safely
                    if (home == null || away == null) continue;

                    // build final gamesummarydto for the frontend
                    result.add(
                            new GameSummaryDto(
                                    gameId,
                                    startTime,
                                    status,
                                    home,
                                    away,
                                    quarter,
                                    clock
                            )
                    );
                }
                return result;

        } catch (Exception e) {
            // if ESPN fails or parsing return an empty list
            return List.of();
        }
    }

    // helper method to convert string to integer
    private Integer parseNullableInt(String s) {
        if (s == null || s.isBlank()) return null;

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // return null if the string is null or blank
    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

}
