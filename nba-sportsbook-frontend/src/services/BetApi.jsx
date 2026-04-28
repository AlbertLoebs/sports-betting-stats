const BASE_URL = "http://localhost:8080";

export async function placeBet(betData) {
    const response = await fetch (`${BASE_URL}/api/bets/place`, {
        method : "POST",
        credentials : "include",
        headers : { 
            "content-type" : "application/json"
        },
        body : JSON.stringify(betData)
    });

    if (!response.ok){
        const errText = await response.text();
        throw new Error(errText || "Failed to place bet");
        
    }

    return await response.json();
}

export async function fetchBetHistory() {
    const res = await fetch(`${BASE_URL}/api/bets/history`, {
        credentials : "include"
    });

    if (!res.ok) {
        throw new Error("Failed to fetch bet history");
    }

    return res.json();
}