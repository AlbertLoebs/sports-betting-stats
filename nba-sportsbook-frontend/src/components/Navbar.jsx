import BalanceDisplay from "./BalanceDisplay";
import "./Navbar.css";
import { Link } from "react-router-dom"
import { logoutUser } from "../services/AuthApi";

function Navbar({ balance, setBalance, currentUser, setCurrentUser }) {
    return (
        <div className="navbar">
            <div className="nav-left">
                <Link to="/" className="nav-link">NBA Sportsbook</Link>
                <Link to="/history" className="nav-link">Bet History</Link>
            </div>

            <div className="nav-right">
                <BalanceDisplay balance={balance} setBalance={setBalance} />
                {currentUser && (
                    <button
                        className="logout-button"
                        onClick={async () => {
                            await logoutUser();
                            setCurrentUser(null);
                        }}
                    >
                        Logout
                    </button>
                )}
            </div>
        </div>
    )
}

export default Navbar;