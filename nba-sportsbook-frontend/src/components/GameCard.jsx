import './GameCard.css';

function GameCard({ game, odds, betSlip }) {

    // convert the time from the backend into readable time
    function formatTime(timeString) {
        const date = new Date(timeString);
        return date.toLocaleString([], {
            hour: "numeric",
            minute: "2-digit"
        });
    }

    // show a - when score is null
    function scoreOrDash(score) {
        return score ?? "-";
    }

    // format odds to show positive +
    function formatOdds(price) {
        if (price == null) return "-";
        return price > 0 ? `+${price}` : `${price}`;
    }

    // format quarters/ot
    function quarterFormat(q) {
        if (q == null || q === 0) return null;
        if (q <= 4) return `Q${q}`;
        if (q === 5) return "OT";
        else return `${q - 4}OT`;
    }

    // build live display string
    function live() {
        const status = (game.status || "").toLowerCase();
        const isLive = status.includes("progress") || status.includes("live");

        if (!isLive) return null;

        const quarterLabel = quarterFormat(game.quarter);
        if (!quarterLabel || !game.clock) return null;

        return `${quarterLabel} ${game.clock}`;
    }

    const liveInfo = live();

    // if the game is over show "Final"
    function gameTimeDisplay() {
        const status = (game.status || "").toLowerCase();

        if (status.includes("final")) {
            return "Final";
        }

        return formatTime(game.startTime);
    }

    return (
        <div className="game-card">

            {/* Top section status and start time */}
            <div className="game-top">

                <span className="status-badge">
                    {game.status}
                </span>

                {/* if game is live show quarter + clock */}
                {liveInfo ? (
                    <span className="live-time">
                        {liveInfo}
                    </span>
                ) : (
                    <span className="start-time">
                        {gameTimeDisplay()}
                    </span>
                )}

            </div>

            {/* Teams section */}
            <div className="teams-list">

                {/* Away team */}
                <div className="team-row">

                    <div className="team-name">
                        {game.awayTeam.logo && (
                            <img
                                src={game.awayTeam.logo}
                                alt={`${game.awayTeam.displayName} logo`}
                                className="team-logo"
                            />
                        )}
                        <span>{game.awayTeam.displayName}</span>
                    </div>

                    <div className="team-score">
                        {scoreOrDash(game.awayTeam.score)}
                    </div>

                    <button
                        className="team-odds-button"
                        onClick={() =>
                            betSlip({
                                gameId : game.gameId,
                                matchup : `${game.awayTeam.displayName} @ ${game.homeTeam.displayName}`,
                                teamName : game.awayTeam.displayName,
                                odds : odds?.moneyline?.awayPrice
                            })
                        }
                        disabled={!odds || odds.moneyline.awayPrice == null}
                        >
                        {odds ? formatOdds(odds.moneyline.awayPrice) : "-"}
                    </button>
                </div>

                {/* Home team */}
                <div className="team-row">

                    <div className="team-name">
                        {game.homeTeam.logo && (
                            <img
                                src={game.homeTeam.logo}
                                alt={`${game.homeTeam.displayName} logo`}
                                className="team-logo"
                            />
                        )}
                        <span>{game.homeTeam.displayName}</span>
                    </div>

                    <div className="team-score">
                        {scoreOrDash(game.homeTeam.score)}
                    </div>

                    <button
                        className="team-odds-button"
                        onClick={() =>
                            betSlip({
                                gameId : game.gameId,
                                matchup : `${game.awayTeam.displayName} @ ${game.homeTeam.displayName}`,
                                teamName : game.homeTeam.displayName,
                                odds : odds?.moneyline?.homePrice
                            })
                        }
                        disabled={!odds || odds.moneyline.homePrice == null}
                        >
                        {odds ? formatOdds(odds.moneyline.homePrice) : "-"}
                    </button>
               </div>
            </div>
        </div>
    );
}

export default GameCard;