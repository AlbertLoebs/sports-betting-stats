import { useEffect, useState } from "react";
import { fetchBalance, depositBalance } from "../services/BalanceApi";
import "./BalanceDisplay.css";

function BalanceDisplay() {
    // stores curr balance val
    const [balance, setBalance] = useState(null);

    // controls modal vis
    const [showModal, setShowModal] = useState(false);

    // amount user enters to depo
    const [amount, setAmount] = useState("");

    // error msg for ui
    const [error, setError] = useState("");

    // loading state when deposit request is running
    const [loading, setLoading] = useState(false);

    // Load balance when component first renders
    useEffect(() => {
        loadBalance();
    }, []);

    // Fetch balance from backend
    async function loadBalance() {
        try {
            const data = await fetchBalance();
            console.log("balance response:", data);
            setBalance(data.balance);
        } catch (err) {
            console.error(err);
            setError("Failed to load balance");
        }
    }

    // Handles deposit form submission
    async function handleDeposit(e) {
        e.preventDefault();

        // simple validation
        if (!amount || Number(amount) <= 0) {
            setError("Enter a valid amount");
            return;
        }

        try {
            setLoading(true);

            // call backend deposit endpoint
            const data = await depositBalance(Number(amount));

            // update displayed balance
            setBalance(data.balance);

            // reset modal state
            setAmount("");
            setShowModal(false);
            setError("");

        } catch (err) {
            console.error(err);
            setError("Deposit failed");
        } finally {
            setLoading(false);
        }
    }

    return (
        <>
            {/* Top right balance display */}
            <div className="balance-widget">

                <span className="balance-text">
                    Balance: {balance !== null ? `$${Number(balance).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}` : "..."}                </span>

                {/* Opens deposit modal */}
                <button
                    className="deposit-button"
                    onClick={() => setShowModal(true)}
                >
                    +
                </button>

            </div>


            {/* Deposit Modal */}
            {showModal && (

                <div
                    className="modal-overlay"
                    onClick={() => setShowModal(false)}
                >

                    {/* Prevent closing when clicking inside modal */}
                    <div
                        className="deposit-modal"
                        onClick={(e) => e.stopPropagation()}
                    >

                        <h3>Add Funds</h3>

                        <form onSubmit={handleDeposit}>

                            {/* Deposit amount input */}
                            <input
                                type="number"
                                placeholder="Enter amount"
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                            />

                            {error && (
                                <p className="error-text">{error}</p>
                            )}

                            <div className="modal-buttons">

                                <button
                                    type="button"
                                    onClick={() => setShowModal(false)}
                                >
                                    Cancel
                                </button>

                                <button
                                    type="submit"
                                    disabled={loading}
                                >
                                    {loading ? "Adding..." : "Deposit"}
                                </button>

                            </div>

                        </form>

                    </div>

                </div>

            )}

        </>
    );
}

export default BalanceDisplay;