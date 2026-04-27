import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { fetchGameDetails } from "../services/GameDetailsApi";
import "./GameDetailsPage.css";

function GameDetailsPage(){
    const { gameId } = useParams();
    // stores game details response
    const [gameDetails, setGameDetails] = useState(null);
    const [loading, setLoading] = useState(true); 
    const [error, setError] = useState("");

    // fetch when loads
    useEffect(() => {
        async function loadGameDetails() {
            try {
                const data = await fetchGameDetails(gameId);
                setGameDetails(data);
            } catch (err) {
                setError("Failed to load game details");
            } finally {
                setLoading(false);
            }
        }
        loadGameDetails();
    }, [gameId]);

    // loading ui
    if (loading) {
        return <p>Loading game details</p>;
    }

    // error ui
    if (error) {
        return <p>{error}</p>
    }

    return (
        <div className="game-details-page">

        {/* page title */}
        <h1> {gameDetails.awayTeam} @ {gameDetails.homeTeam} </h1>

        { /* game status */}
        <p className="game-status">
            {gameDetails.status}
        </p>

        {/* loop through both teams */}
            {gameDetails.teams.map((team) => (
                <div key={team.abbreviation} className="team-section">

                    {/* team name */}
                    <h2>{team.teamName}</h2>

                    {/* player stats table */}
                    <table className="stats-table">
                        <thead>
                        <tr>
                            <th>Player</th>
                            <th>MIN</th>
                            <th>PTS</th>
                            <th>REB</th>
                            <th>AST</th>
                            <th>STL</th>
                            <th>BLK</th>
                        </tr>
                        </thead>

                        <tbody>
                        {team.players.map((player) => (
                            <tr key={player.name}>
                                <td className="player-cell">

                                    {/* player headshot */}
                                    <img
                                        src={player.headshotUrl}
                                        alt={player.name}
                                        className="player-headshot"
                                    />

                                    {player.name}
                                </td>

                                <td>{player.minutes}</td>
                                <td>{player.points}</td>
                                <td>{player.rebounds}</td>
                                <td>{player.assists}</td>
                                <td>{player.steals}</td>
                                <td>{player.blocks}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            ))}
        </div>
    );
}

export default GameDetailsPage;