// fetch games from the backend
// returns an array of gamesummary dto objects

// fetch games from a date from backend
// date is a string like 2026-02-20
export async function fetchGamesByDate(date) {
    const response = await fetch(`http://localhost:8080/api/games/by-date?date=${date}`)

    if (!response.ok){
        throw new Error("failed to fetch games");
    }

    return response.json();
}

export async function fetchTodaysGames (){

    const response = await fetch("http://localhost:8080/api/games/today")

    if (!response.ok) {
        throw new Error("Failed to fetch games")
    }

    return response.json();

}

// fetch todays nba games from spring boot 
export async function fetchOddsByDate(date){
    const response = await fetch(`http://localhost:8080/api/odds/by-date?date=${date}`);

    if (!response.ok) {
        throw new Error("Failed to fetch game odds");
    }
    
    return response.json();
}