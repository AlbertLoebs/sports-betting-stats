

// fetch games from the backend
// returns an array of gamesummary dto objects
export async function fetchTodaysGames (){

    const response = await fetch("http://localhost:8080/api/games/today")

    if (!response.ok) {
        throw new error("Failed to fetch games")
    }

    return response.json();

}