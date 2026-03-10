import BalanceDisplay from "./BalanceDisplay";
import "./Navbar.css";

function Navbar(){
    return (
        <div className="navbar">
            {/* left side: site title */}
            <div className="nav-left">
                NBA Sportsbook
            </div>

            {/* Right side: balance widget */}
            <div className="nav-right">
                <BalanceDisplay />
            </div>
        </div>
    )
}

export default Navbar;