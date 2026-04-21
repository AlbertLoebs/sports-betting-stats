import './GameCard.css';

function GameCard({ game, odds, onAddToBetSlip }) {

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

    function formatLine(line) {
        if (line == null) return "-";
        return line > 0 ? `+${line}` : `${line}`;
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

            {/* top section status and start time */}
            <div className="game-top">

                <span className="status-badge">
                    {game.status}
                </span>

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

            {/* teams + odds all on same row */}
            <div className="teams-list">
                <div className="odds-header">
                    <span className="header-left"></span>

                    <div className="header-right">
                        <span></span> {/* score column */}
                        <span>Moneyline</span>
                        <span>Spread</span>
                        <span>Total</span>
                    </div>
                </div>

                {/* away team */}
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

                    <div className="team-right">

                        <div className="team-score">
                            {scoreOrDash(game.awayTeam.score)}
                        </div>

                        <button
                            className="team-odds"
                            onClick={() =>
                                onAddToBetSlip({
                                    gameId: game.gameId,
                                    matchup: `${game.awayTeam.displayName} @ ${game.homeTeam.displayName}`,
                                    selection: game.awayTeam.displayName,
                                    betType: "moneyline",
                                    odds: odds?.moneyline?.awayPrice
                                })
                            }
                            disabled={!odds || odds?.moneyline?.awayPrice == null}
                        >
                            {formatOdds(odds?.moneyline?.awayPrice)}
                        </button>

                        <button
                            className="team-odds"
                            onClick={() =>
                                onAddToBetSlip({
                                    gameId: game.gameId,
                                    matchup: `${game.awayTeam.displayName} @ ${game.homeTeam.displayName}`,
                                    selection: game.awayTeam.displayName,
                                    betType: "spread",
                                    line: odds?.spread?.awayPoint,
                                    odds: odds?.spread?.awayPrice
                                })
                            }
                            disabled={!odds || odds?.spread?.awayPrice == null || odds?.spread?.awayPoint == null}
                        >
                            {formatLine(odds?.spread?.awayPoint)} ({formatOdds(odds?.spread?.awayPrice)})
                        </button>

                        <button
                            className="team-odds"
                            onClick={() =>
                                onAddToBetSlip({
                                    gameId: game.gameId,
                                    matchup: `${game.awayTeam.displayName} @ ${game.homeTeam.displayName}`,
                                    selection: "Over",
                                    betType: "total",
                                    line: odds?.total?.line,
                                    odds: odds?.total?.overPrice
                                })
                            }
                            disabled={!odds || odds?.total?.line == null || odds?.total?.overPrice == null}
                        >
                            O {odds?.total?.line} ({formatOdds(odds?.total?.overPrice)})
                        </button>
                    </div>
                </div>

                {/* home team */}
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

                    <div className="team-right">

                        <div className="team-score">
                            {scoreOrDash(game.homeTeam.score)}
                        </div>

                        <button
                            className="team-odds"
                            onClick={() =>
                                onAddToBetSlip({
                                    gameId: game.gameId,
                                    matchup: `${game.awayTeam.displayName} @ ${game.homeTeam.displayName}`,
                                    selection: game.homeTeam.displayName,
                                    betType: "moneyline",
                                    odds: odds?.moneyline?.homePrice
                                })
                            }
                            disabled={!odds || odds?.moneyline?.homePrice == null}
                        >
                            {formatOdds(odds?.moneyline?.homePrice)}
                        </button>

                        <button
                            className="team-odds"
                            onClick={() =>
                                onAddToBetSlip({
                                    gameId: game.gameId,
                                    matchup: `${game.awayTeam.displayName} @ ${game.homeTeam.displayName}`,
                                    selection: game.homeTeam.displayName,
                                    betType: "spread",
                                    line: odds?.spread?.homePoint,
                                    odds: odds?.spread?.homePrice
                                })
                            }
                            disabled={!odds || odds?.spread?.homePrice == null || odds?.spread?.homePoint == null}
                        >
                            {formatLine(odds?.spread?.homePoint)} ({formatOdds(odds?.spread?.homePrice)})
                        </button>

                        <button
                            className="team-odds"
                            onClick={() =>
                                onAddToBetSlip({
                                    gameId: game.gameId,
                                    matchup: `${game.awayTeam.displayName} @ ${game.homeTeam.displayName}`,
                                    selection: "Under",
                                    betType: "total",
                                    line: odds?.total?.line,
                                    odds: odds?.total?.underPrice
                                })
                            }
                            disabled={!odds || odds?.total?.line == null || odds?.total?.underPrice == null}
                        >
                            U {odds?.total?.line} ({formatOdds(odds?.total?.underPrice)})
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default GameCard;