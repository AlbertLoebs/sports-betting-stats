// fetch games from the backend
// returns an array of gamesummary dto objects
export async function fetchTodaysGames (){

    const response = await fetch("http://localhost:8080/api/games/today")

    if (!response.ok) {
        throw new error("Failed to fetch games")
    }

    return response.json();

}

// fetch todays nba games from spring boot 
export async function fetchTodaysOdds(){
    const response = await fetch("http://localhost:8080/api/odds/today");

    if (!response.ok) {
        throw new error("Failed to fetch game odds");
    }
    
    return response.json();
}