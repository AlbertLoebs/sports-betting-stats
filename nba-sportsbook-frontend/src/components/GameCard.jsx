
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

    // format quarters/ot
    function quarterFormat(q){
        if (q == null || q === 0) return null;
        if (q <= 4) return `Q${q}`;
        if (q === 5) return "OT";
        else return `${q - 4}OT`;
    }

    // build live display string
    function live(){
        const status = (game.status || "").toLowerCase();
        const isLive = status.includes("progress") || status.includes("live");

        if (!isLive) return null;

        const quarterLabel = quarterFormat(game.quarter);
        if (!quarterLabel || !game.clock) return null;
        return `${quarterLabel} ${game.clock}`;
    }

    const liveInfo = live();

    return(
    // key must be unique use the game id
    <div key={game.gameId} className="game-card">

        {/* Top section status and start time */}
        <div className="game-top">
            <span className="status-badge">
                {game.status}
            </span>

            {/* if game is live, show quarter + clock */}
            {liveInfo ? (
                <span className="live-time">
                    {liveInfo}
                </span>
            ) : (
            <span className="start-time">
                {formatTime(game.startTime)}
            </span>
            )}
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