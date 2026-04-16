import { useEffect, useState } from "react";
import { fetchBetHistory } from "../services/BetApi";
import "./BetHistoryPage.css";

function BetHistoryPage() {
    const [bets, setBets] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function load() {
            const data = await fetchBetHistory();
            setBets(data);
            setLoading(false);
        }
        load();
    }, [])

    if (loading) return <p>Loading...</p>;

    function formatOdds(odds) {
        return odds > 0 ? `+${odds}` : `${odds}`;
    }

    function formatCents(cents) {
        return (cents / 100).toLocaleString("en-US", {
            style: "currency",
            currency: "USD"
        });
    }

    return (
    <div className="bet-history-page">
        <h1>Bet History</h1>

        {bets.length === 0 ? (
            <p>No bets yet</p>
        ) : (
            <div className="bet-history-list">
                {bets.map((bet) => (
                    <div key={bet.id} className="bet-card">

                        <div className="bet-header">
                            <h3>Bet #{bet.id}</h3>
                            <span className={`bet-status ${bet.status.toLowerCase()}`}>
                                {bet.status}
                            </span>
                        </div>

                        <p><strong>Wager:</strong> {formatCents(bet.wagerCents)}</p>

                        <p><strong>Odds:</strong> {formatOdds(bet.combinedOdds)}</p>

                        <p><strong>Potential Payout:</strong> {formatCents(bet.potentialPayoutCents)}</p>

                        <p>
                            <strong>Type:</strong>{" "}
                            {bet.legs.length === 1 ? "Single" : `${bet.legs.length}-leg parlay`}
                        </p>

                        <div className="bet-legs">
                            <h4>Legs</h4>

                            {bet.legs.map((leg, index) => (
                                <div key={leg.id} className="bet-leg-row">
                                    <span>
                                        Leg {index + 1}: {leg.team} ({formatOdds(leg.odds)})
                                    </span>

                                    <span className={`leg-status ${leg.status.toLowerCase()}`}>
                                        {leg.status}
                                    </span>
                                </div>
                            ))}
                        </div>

                    </div>
                ))}
            </div>
        )}
    </div>
);
}

export default BetHistoryPage;