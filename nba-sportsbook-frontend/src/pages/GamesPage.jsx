import { useEffect, useState } from "react";
import { fetchTodaysGames } from "../services/GamesApi";
import GameCard from "../components/GameCard";
import './GamesPage.css'

function GamesPage() {
  // vars rendered
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // fetch and update state
  async function loadGames() {

    try {
      // when starting a fetch show loading and clear any previous error
      setLoading(true);
      setError(null);

      // call api layer
      const data = await fetchTodaysGames();

      // update state with data from backend, this triggers a re render
      setGames(data);

    } catch (error) {
      // if anything fails
      setError(error.message || "Failed to load")
    } finally {
      // will run with pass or fail, stops loading
      setLoading(false);
    }
  }

  // this runs affect componet is first rendered,
  //  [] means only run when componet mounts
  useEffect(() => {
    loadGames();
  }, [])

  // UI states 
  // if loading show loading
  if (loading) {
    return (
      <div>
        <h1>Today's games</h1>
        <p>loading games...</p>
      </div>
    );
  }

  // if error show error
  if (error) {
    return (
      <div>
        <h1>Today's games</h1>
        <p>Error : {error} </p>
      </div>
    );
  }

  // the fetch worked but array is empty due to no games
  if (games.length === 0) {
    return (
      <div>
        <h1>Today's games</h1>
        <p>There are no games today</p>
      </div>
    );
  }

  // main
  return (
    <div className="games-page">

      {/* Header section */}
      <div className="games-header">
        <h1>Today's Games</h1>
        <button onClick={loadGames}>Refresh</button>
      </div>

      {/* Grid layout for game cards */}
      <div className="games-grid">
        
        {/* Loop through the games array and render one GameCard for each game */}
        {games.map((game) => (
          <GameCard key={game.gameId} game={game} />
        ))}

      </div>
    </div>
  );
}

export default GamesPage