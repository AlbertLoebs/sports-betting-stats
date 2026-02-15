
function GameCard({ game }) {

    // convert the time from the backend into readable time
    function formatTime(timeString) {
        const date = new Date(timeString)
        return date.toLocaleString();
    }

    // show a - when score is null
    function scoreOrDash(score) {
        return score ?? "-";
    }

    return(
    // key must be unique use the game id
    <div key={game.gameId} className="game-card">

        {/* Top section status and start time */}
        <div className="game-top">
            <span className="status-badge">
                {game.status}
            </span>

            <span className="start-time">
                {formatTime(game.startTime)}
            </span>
        </div>

        {/* Teams section */}
        <div className="teams-row">

            {/* Team names */}
            <div className="team-names">
                <div className="team-name">
                    {game.awayTeam.displayName}
                </div>
                <div className="team-name">
                    {game.homeTeam.displayName}
                </div>
            </div>

            {/* Scores */}
            <div className="team-scores">
                <div className="team-score">
                    {scoreOrDash(game.awayTeam.score)}
                </div>
                <div className="team-score">
                    {scoreOrDash(game.homeTeam.score)}
                </div>
            </div>

        </div>

    </div>
    )
}

export default GameCard