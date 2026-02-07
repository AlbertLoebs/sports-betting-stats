import { useState } from 'react'
import { useEffect } from 'react'
import './App.css'

function App() {
  const [message, setMessage] = useState("Loading...")

  useEffect(() => {
    fetch("http://localhost:8080/api/games/today")
    .then((res) => res.text())
    .then((data) => setMessage(data))
    .catch((err) => {
      console.error(err);
      setMessage("Error connecting to the backend");
    });

  },[]);

  return (
    <div>
      <h1>Test</h1>
      <p>{message}</p>
    </div>
  );
}

export default App
