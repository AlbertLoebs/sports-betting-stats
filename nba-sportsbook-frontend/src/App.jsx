import './App.css'
import GamesPage from "./pages/GamesPage";
import Navbar from "./components/Navbar";
import { useState, useEffect } from "react";
import { fetchBalance } from './services/BalanceApi';
import BetHistoryPage from './pages/BetHistoryPage';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

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
    <BrowserRouter>
      <Navbar balance={balance} setBalance={setBalance}/>

      <Routes>
        <Route path="/" element={<GamesPage setBalance={setBalance} />} />
        <Route path="/history" element={<BetHistoryPage />} />
      </Routes>

    </BrowserRouter>
    
  );
}

export default App;