import { useEffect, useState } from "react";
import { fetchBetHistory } from "../services/BetApi";

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

    return (
        <div>
            <h1>Bet History</h1>

            {bets.length === 0 ? (
                <p>No bets yet</p>
            ) : (
                bets.map((bet) => (
                    <div key={bet.id}>
                        <h3>Bet #{bet.id}</h3>
                        <p>Wager: {bet.wagerCents}</p>
                        <p>Status: {bet.status}</p>

                        <div>
                            {bet.legs.map((leg) => (
                                <div key={leg.id}>
                                    {leg.team} ({leg.odds})
                                </div>
                            ))}
                        </div>
                        </div>
                ))
            )}
        </div>
    )

}

export default BetHistoryPage;