import { useEffect, useState, useRef } from "react";
import { fetchGamesByDate } from "../services/GamesApi";
import { fetchOddsByDate } from "../services/GamesApi";
import GameCard from "../components/GameCard";
import './GamesPage.css'

function GamesPage() {
  // vars rendered
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // stores odds
  const [odds, setOdds] = useState([]);

  // store current interval id so we can clear it
  const intervalRef = useRef(null);

  // tracks the newest games request
  const gamesRequestIdRef = useRef(0);

  // tracks the newest odds request
  const oddsRequestIdRef = useRef(0);

  // stores selected date as a Date object
  const [selectedDate, setSelectedDate] = useState(new Date());

  function isLiveGame(game) {
    const s = (game.status || "").toLowerCase();
    return s.includes("progress") || s.includes("live");
  }

  function formatDateForApi(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  }

  // returns true if the selected date is today or in the future
  function isTodayOrFuture(date) {
    const selected = new Date(date);
    const today = new Date();

    // remove time so only the calendar day is compared
    selected.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);

    return selected >= today;
  }

  // returns a new date that is a certain number of days away from today
  function getDateOffsetFromToday(days) {
    const date = new Date();
    date.setHours(0, 0, 0, 0);
    date.setDate(date.getDate() + days);
    return date;
  }

  // checks if two dates are the same calendar day
  function isSameDay(date1, date2) {
    return (
      date1.getFullYear() === date2.getFullYear() &&
      date1.getMonth() === date2.getMonth() &&
      date1.getDate() === date2.getDate()
    );
  }

  const minDate = getDateOffsetFromToday(-7);
  const maxDate = getDateOffsetFromToday(7);

  const atMinDate = isSameDay(selectedDate, minDate);
  const atMaxDate = isSameDay(selectedDate, maxDate);

  // helper to move selected date by a number of days
  function changeDate(days) {
    setSelectedDate((prevDate) => {
      const newDate = new Date(prevDate);
      newDate.setDate(newDate.getDate() + days);
      newDate.setHours(0, 0, 0, 0);

      if (newDate < minDate) {
        return minDate;
      }

      if (newDate > maxDate) {
        return maxDate;
      }

      return newDate;
    });
  }

  function normalizeTeamName(name) {
    const normalized = name.toLowerCase().trim();

    const teamNameMap = {
      "la clippers": "los angeles clippers"
    };

    return teamNameMap[normalized] || normalized;
  }

  // formats the selected date nicely for the page header
  function formatDateForDisplay(date) {
    return date.toLocaleDateString(undefined, {
      weekday: "long",
      month: "long",
      day: "numeric"
    });
  }

  // Fetch games for a specific date from the backend.
  // A unique request id is used so that if multiple requests happen
  // (for example when changing dates quickly or polling), only the
  // newest response is allowed to update the state.
  async function loadGames(date, showLoading = false) {
    // create a unique id for this request
    const requestId = ++gamesRequestIdRef.current;

    try {
      if (showLoading) {
        setLoading(true);
      }

      setError(null);

      const formattedDate = formatDateForApi(date);
      const data = await fetchGamesByDate(formattedDate);

      // only update state if this is still the newest request
      if (requestId === gamesRequestIdRef.current) {
        setGames(data);
      }

    } catch (error) {
      if (requestId === gamesRequestIdRef.current) {
        setError(error.message || "Failed to load games");
      }
    } finally {
      if (showLoading && requestId === gamesRequestIdRef.current) {
        setLoading(false);
      }
    }
  }

  // Fetch betting odds for the selected date.
  // Like loadGames(), this uses a request id so that if multiple
  // requests overlap, only the newest response updates the UI
  async function loadOdds(date) {
    // unique id for this request
    const requestId = ++oddsRequestIdRef.current;
    try {
      if (!isTodayOrFuture(date)) {
        if (requestId === oddsRequestIdRef.current) {
          setOdds([]);
        }
        return;
      }

      const formattedDate = formatDateForApi(date);
      const data = await fetchOddsByDate(formattedDate);

      // only update state if this is still the newest request
      if (requestId === oddsRequestIdRef.current) {
        setOdds(data);
      }

    } catch (error) {
      console.log("Failed to load odds:", error);

      if (requestId === oddsRequestIdRef.current) {
        setOdds([]);
      }
    }
  }

  // whenever the selected date changes, fetch new games and odds.
  // requestId protection prevents stale responses from older requests.
  useEffect(() => {
    loadGames(selectedDate, true);
    loadOdds(selectedDate);
  }, [selectedDate])

  // polling effect
  useEffect(() => {
    const anyLive = games.some(isLiveGame);

    // poll faster if any game is live, slower otherwise
    const pollRate = anyLive ? 15000 : 60000;

    // clear old interval before starting a new one
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
    }

    // poll silently with no loading flicker
    intervalRef.current = setInterval(() => {
      loadGames(selectedDate, false);

      // only poll odds for today/future
      if (isTodayOrFuture(selectedDate)) {
        loadOdds(selectedDate);
      }
    }, pollRate);

    // cleanup
    return () => {
      clearInterval(intervalRef.current);
    };
  }, [games, selectedDate])

  // UI states 
  // if loading show loading
  if (loading) {
    return (
      <div>
        <h1>Games</h1>
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

  // main
  return (
    <div className="games-page">

      {/* Header section */}
      <div className="games-header">
        <h1>{formatDateForDisplay(selectedDate)}</h1>

        {/* date nav buttons */}
        <div className="date-controls">
          <button onClick={() => changeDate(-1)} disabled={atMinDate}>
            Previous
          </button>

          <button onClick={() => setSelectedDate(new Date())}>
            Today
          </button>

          <button onClick={() => changeDate(1)} disabled={atMaxDate}>
            Next
          </button>

          <button onClick={() => {
            loadGames(selectedDate, false);
            loadOdds(selectedDate)
          }}>
            Refresh
          </button>
        </div>
      </div>

      {/* no games for this day */}
      {games.length === 0 ? (
        <p>There are no games on this day.</p>
      ) : (

        <div className="games-grid">

          {/* render one GameCard per game */}
          {games.map((game) => {

            // find matching odds by team names
            const matchingOdds = odds.find(
              (oddsGame) =>
                normalizeTeamName(oddsGame.homeTeam) === normalizeTeamName(game.homeTeam.displayName) &&
                normalizeTeamName(oddsGame.awayTeam) === normalizeTeamName(game.awayTeam.displayName)
            );

            return (
              <GameCard
                key={game.gameId}
                game={game}
                odds={matchingOdds}
              />
            );
          })}
        </div>
      )}
    </div>
  );
}

export default GamesPage