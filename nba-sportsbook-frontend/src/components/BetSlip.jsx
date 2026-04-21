import { useMemo, useState } from "react";
import "./BetSlip.css"
import { placeBet } from "../services/BetApi";

function BetSlip({ selections, onRemoveSelection, onClearSlip, onPlaceBet }) {
    const [wager, setWager] = useState("");
    const [loading, setLoading] = useState(false);
    const [betError, setBetError] = useState("");
    const [betMessage, setBetMessage] = useState("");

    function formatOdds(price) {
        if (price == null) {
            return "-";
        } else {
            return price > 0 ? `+${price}` : `${price}`
        }
    }

    // converting to decimal for easier calc
    function americanToDecimal(americanOdds) {
        if (americanOdds > 0) {
            return 1 + (americanOdds / 100)
        }
        return 1 + (100 / Math.abs(americanOdds))
    }

    function formatSelectionText(selection) {
        if (selection.betType === "spread") {
            return `${selection.selection} ${selection.line > 0 ? "+" : ""}${selection.line}`;
        }

        if (selection.betType === "total") {
            return `${selection.selection} ${selection.line}`;
        }

        return selection.selection;
    }

    // calc deicmal odds for parlay
    const combinedDecimalOdds = useMemo(() => {
        // no selections = 0
        if (selections.length === 0) { return 0 }

        // mult all selections
        return selections.reduce((total, selection) => {
            const decimalOdds = americanToDecimal(selection.odds)

            return total * decimalOdds

        }, 1);
    }, [selections])

    // convert combined decimal odds back 2 American odds (for display)
    const combinedAmericanOdds = useMemo(() => {

        if (combinedDecimalOdds <= 1) return 0;

        if (combinedDecimalOdds >= 2) {
            return Math.round((combinedDecimalOdds - 1) * 100);
        }

        return Math.round(-100 / (combinedDecimalOdds - 1));

    }, [combinedDecimalOdds]);

    // calculate potential payout shown in UI this is only a preview (backend does real calculation)
    const potentialPayout = useMemo(() => {

        const wagerNumber = Number(wager);

        // invalid wager → return 0
        if (!wagerNumber || wagerNumber <= 0 || combinedDecimalOdds <= 1) {
            return 0;
        }

        return (wagerNumber * combinedDecimalOdds).toFixed(2);

    }, [wager, combinedDecimalOdds]);

    // called when user clicks place bet
    async function handlePlaceBet() {
        try {
            setBetError("")
            setBetMessage("")

            const wagerNumber = Number(wager);

            // small frontend validation
            if (selections.length === 0) {
                setBetError("Add at least one selection")
                return;
            }

            if (!wagerNumber || wagerNumber < 0) {
                setBetError("Enter a valid wager")
                return
            }

            setLoading(true);

            // send parlay to the backend
            const result = await onPlaceBet({
                selections: selections.map((selection) => ({
                    gameId: selection.gameId,
                    selection: selection.selection,
                    betType: selection.betType,
                    line: selection.line,
                    odds: selection.odds
                })),
                wager: wagerNumber
            });
            // show a success msg
            setBetMessage("Bet placed!")

            // reset slip
            setWager("")
            onClearSlip();
        } catch (error) {
            setBetError(error.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="bet-slip">

            <h3>Bet Slip</h3>

            {/* if no picks yet */}
            {selections.length === 0 ? (
                <p className="bet-slip-empty">No selections yet.</p>
            ) : (
                <>
                    {/* list of all selected picks */}
                    <div className="bet-slip-list">
                        {selections.map((selection, index) => (
                            <div key={`${selection.gameId}-${selection.teamName}`} className="bet-slip-item">

                                <div className="bet-slip-item-text">
                                    <div className="bet-slip-matchup">
                                        {selection.matchup}
                                    </div>

                                    <div className="bet-slip-pick">
                                        {formatSelectionText(selection)} {formatOdds(selection.odds)}
                                    </div>
                                </div>

                                {/* 🔹 remove individual selection */}
                                <button
                                    className="bet-slip-remove"
                                    onClick={() => onRemoveSelection(index)}
                                >
                                    Remove
                                </button>
                            </div>
                        ))}
                    </div>

                    {/* summary section */}
                    <div className="bet-slip-summary">

                        <p>
                            Combined Odds:{" "}
                            <strong>
                                {combinedAmericanOdds > 0
                                    ? `+${combinedAmericanOdds}`
                                    : combinedAmericanOdds}
                            </strong>
                        </p>

                        {/* wager input */}
                        <input
                            type="number"
                            min="1"
                            step="0.01"
                            value={wager}
                            onChange={(e) => setWager(e.target.value)}
                            placeholder="Enter wager"
                            className="bet-slip-input"
                        />

                        <p>
                            Potential Payout: <strong>${potentialPayout}</strong>
                        </p>

                        {/* action buttons */}
                        <div className="bet-slip-actions">

                            <button onClick={handlePlaceBet} disabled={loading}>
                                {loading ? "Placing..." : "Place Bet"}
                            </button>

                            <button onClick={onClearSlip} disabled={loading}>
                                Clear
                            </button>
                        </div>
                    </div>
                </>
            )}

            {/* 🔹 feedback messages */}
            {betMessage && <p className="bet-slip-message">{betMessage}</p>}
            {betError && <p className="bet-slip-error">{betError}</p>}
        </div>
    );
}

export default BetSlip