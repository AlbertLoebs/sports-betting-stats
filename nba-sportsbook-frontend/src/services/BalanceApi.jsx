const BASE_URL = "http://localhost:8080/api/balance";

export async function fetchBalance(){
    const response = await fetch("http://localhost:8080/api/balance", {
        credentials: "include"
    });

    if (!response.ok){
        throw new Error("Failed to fetch balance");
    }

    return response.json();
}

// send depo req to the backend
export async function depositBalance(amount) {
    const res = await fetch(`${BASE_URL}/deposit`, {
        method: "POST",
        credentials: "include",
        headers: {
            "content-type" : "application/json"
        },
        // json body sent to spring boot controller
        body: JSON.stringify({amount})
    });

    if (!res.ok) {
        throw new Error("Failed to deposit funds");
    }
    return res.json();
}