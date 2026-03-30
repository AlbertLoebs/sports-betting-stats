import './App.css'
import GamesPage from "./pages/GamesPage";
import Navbar from "./components/Navbar";
import { useState, useEffect } from "react";
import { fetchBalance } from './services/BalanceApi';

function App() {
   // shared balance state for whole app
  const [balance, setBalance] = useState(null);

  // load balance once when app starts
  useEffect(() => {
    async function loadBalance() {
      try {
        console.log("loading balance...");
        const data = await fetchBalance();
        console.log("App balance response:", data);
        setBalance(data.balance);
      } catch (err) {
        console.error("Failed to load balance:", err);
      }
    }

    loadBalance();
  }, []);

  return (
    <>
      <Navbar balance={balance} setBalance={setBalance} />
      <GamesPage setBalance={setBalance} />
    </>
  );
}

export default App;