import { useState } from 'react'
import { useEffect } from 'react'
import { fetchTodaysGames } from './services/GamesApi'
import './App.css'

function App() {
  const [message, setMessage] = useState("Loading...")

  useEffect(() => {
    async function testFetch() {
      try {
        const data = await fetchTodaysGames();
        console.log("Games from the backend:", data);
        setMessage(`Fetched ${data.length} games. Check console.`);
      } catch (error) {
        console.error("Error fetching games:", error);
        setMessage("Fetch failed. Check console for error.");
      }
    }

    testFetch(); 
  }, []);

  return (
    <div>
      <h1>Test</h1>
      <p>{message}</p>
    </div>
  );
}

export default App
