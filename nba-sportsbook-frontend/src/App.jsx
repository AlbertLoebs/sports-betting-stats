import './App.css'
import GamesPage from "./pages/GamesPage";
import Navbar from "./components/Navbar";
import { useState, useEffect } from "react";
import { fetchBalance } from './services/BalanceApi';
import BetHistoryPage from './pages/BetHistoryPage';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { fetchCurrentUser } from './services/AuthApi';
import LoginPage from './pages/LoginPage';
import GameDetailsPage from './pages/GameDetailsPage';

function App() {
   // shared balance state for whole app
  const [balance, setBalance] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);

  // runs when app starts, checks if user has a session

useEffect(() => {
  async function loadCurrentUser() {
    try {
      const user = await fetchCurrentUser();
      setCurrentUser(user);
    } catch (err) {
      console.error("Failed to load user", err);
    } finally {
      setLoadingUser(false);
    }
  }
  loadCurrentUser();
}, []);

  // load balance once when user is logged in
  useEffect(() => {
    async function loadBalance() {
      try {
        const data = await fetchBalance();
        setBalance(data.balance);
      } catch (err) {
        console.error("Failed to load balance:", err);
      }
    }

    if (currentUser) {
      loadBalance();
    }
  }, [currentUser]);

  // While checking session → show loading
  if (loadingUser) {
    return <p>Loading...</p>;
  }

// not logged in show login page
  if (!currentUser) {
    return <LoginPage setCurrentUser={setCurrentUser} />;
  }

  return (
    <BrowserRouter>
      <Navbar balance={balance} setBalance={setBalance} currentUser={currentUser} setCurrentUser={setCurrentUser}/>

      <Routes>
        <Route path="/" element={<GamesPage setBalance={setBalance} />} />
        <Route path="/history" element={<BetHistoryPage />} />
        <Route path="/games/:gameId" element={<GameDetailsPage />} />
      </Routes>

    </BrowserRouter>
    
  );
}

export default App;