export async function placeBet(betData) {
    const response = await fetch("http://localhost:8080/api/bets/place", {
        method : "POST",
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