const API_BASE = "http://localhost:8080/api/games";

export async function fetchGameDetails(gameId) {
    const response = await fetch(`${API_BASE}/${gameId}/boxscore`);

    if (!response.ok){
        throw new Error("Failed to fetch game details");
    }

    return response.json();
}