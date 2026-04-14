import BalanceDisplay from "./BalanceDisplay";
import "./Navbar.css";
import { Link } from "react-router-dom"

function Navbar({balance, setBalance}){
    return (
         <div className="navbar">
            <div className="nav-left">
                <Link to="/" className="nav-link">NBA Sportsbook</Link>
                <Link to="/history" className="nav-link">Bet History</Link>
            </div>

            <div className="nav-right">
                <BalanceDisplay balance={balance} setBalance={setBalance} />
            </div>
        </div>
    )
}

export default Navbar;