package com.sportsbook.nba.games.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.sportsbook.nba.games.dto.GameBoxScoreDto;
import com.sportsbook.nba.games.dto.PlayerStatsDto;
import com.sportsbook.nba.games.dto.TeamPlayerStatsDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameDetailsService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getStat(JsonNode stats, int index) {
        if (stats == null || !stats.isArray() || stats.size() <= index || stats.get(index) == null) {
            return "-";
        }

        return stats.get(index).asText();
    }

    private TeamPlayerStatsDto fetchRosterTeam(JsonNode competitor) throws Exception {
        String teamId = competitor.path("team").path("id").asText();
        String teamName = competitor.path("team").path("displayName").asText();
        String abbreviation = competitor.path("team").path("abbreviation").asText();

        String rosterUrl =
                "https://site.api.espn.com/apis/site/v2/sports/basketball/nba/teams/"
                        + teamId
                        + "/roster";

        String response = restClient.get()
                .uri(rosterUrl)
                .retrieve()
                .body(String.class);

        JsonNode rosterRoot = objectMapper.readTree(response);

        List<PlayerStatsDto> players = new ArrayList<>();

        JsonNode athletes = rosterRoot.path("athletes");

        for (JsonNode athlete : athletes) {

            String name = athlete.path("displayName").isMissingNode()
                    ? "Unknown Player"
                    : athlete.path("displayName").asText();

            String headshotUrl = athlete.path("headshot").path("href").isMissingNode()
                    ? ""
                    : athlete.path("headshot").path("href").asText();

            String position = athlete.path("position").path("abbreviation").isMissingNode()
                    ? "-"
                    : athlete.path("position").path("abbreviation").asText();

            PlayerStatsDto player = new PlayerStatsDto(
                    name,
                    headshotUrl,
                    position,
                    "-", "-", "-", "-", "-", "-", "-", "-", "-"
            );

            players.add(player);
        }

        return new TeamPlayerStatsDto(
                teamName,
                abbreviation,
                players
        );
    }

    public GameBoxScoreDto getGameDetails(String gameId) {

        try {
            String url =
                    "https://site.api.espn.com/apis/site/v2/sports/basketball/nba/summary?event="
                            + gameId;

            String response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);

            String status = root.path("header")
                    .path("competitions")
                    .get(0)
                    .path("status")
                    .path("type")
                    .path("description")
                    .asText();

            JsonNode competitors = root.path("header")
                    .path("competitions")
                    .get(0)
                    .path("competitors");

            String homeTeam = competitors.get(0)
                    .path("team")
                    .path("displayName")
                    .asText();

            String awayTeam = competitors.get(1)
                    .path("team")
                    .path("displayName")
                    .asText();

            List<TeamPlayerStatsDto> teams = new ArrayList<>();

            JsonNode playersNode = root.path("boxscore").path("players");

            // Future games usually do not have boxscore player stats yet.
            // In that case, fetch team rosters and show empty stat values.
            if (playersNode.isMissingNode() || !playersNode.isArray() || playersNode.size() == 0) {
                TeamPlayerStatsDto awayRoster = fetchRosterTeam(competitors.get(1));
                TeamPlayerStatsDto homeRoster = fetchRosterTeam(competitors.get(0));

                teams.add(awayRoster);
                teams.add(homeRoster);

                return new GameBoxScoreDto(
                        gameId,
                        status,
                        homeTeam,
                        awayTeam,
                        teams
                );
            }

            for (JsonNode teamNode : playersNode) {

                String teamName = teamNode.path("team")
                        .path("displayName")
                        .asText();

                String abbreviation = teamNode.path("team")
                        .path("abbreviation")
                        .asText();

                List<PlayerStatsDto> players = new ArrayList<>();

                JsonNode athleteRows = teamNode.path("statistics")
                        .get(0)
                        .path("athletes");

                for (JsonNode playerNode : athleteRows) {

                    String name = playerNode.path("athlete")
                            .path("displayName")
                            .asText();

                    String position = playerNode.path("athlete")
                            .path("position")
                            .path("abbreviation")
                            .asText();

                    JsonNode stats = playerNode.path("stats");

                    String headshotUrl = playerNode.path("athlete")
                            .path("headshot")
                            .path("href")
                            .asText();

                    PlayerStatsDto player = new PlayerStatsDto(
                            name,
                            headshotUrl,
                            position,
                            getStat(stats, 0), // minutes
                            getStat(stats, 1), // points
                            getStat(stats, 5), // rebounds
                            getStat(stats, 6), // assists
                            getStat(stats, 8), // steals
                            getStat(stats, 9), // blocks
                            getStat(stats, 2), // field goals
                            getStat(stats, 3), // threes
                            getStat(stats, 4)  // free throws
                    );

                    players.add(player);
                }

                TeamPlayerStatsDto teamStats = new TeamPlayerStatsDto(
                        teamName,
                        abbreviation,
                        players
                );

                teams.add(teamStats);
            }

            return new GameBoxScoreDto(
                    gameId,
                    status,
                    homeTeam,
                    awayTeam,
                    teams
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch game details", e);
        }
    }
}