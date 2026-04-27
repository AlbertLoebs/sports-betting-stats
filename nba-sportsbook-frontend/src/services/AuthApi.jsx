const API_BASE = "http://localhost:8080/api/auth";

// register new user
export async function registerUser(username, password) {
    const response = await fetch(`${API_BASE}/register`, {
        method: "POST",
        credentials: "include",
        headers : {
            "Content-Type" : "application/json"
        },
        body: JSON.stringify({username,password})
    });

    if(!response.ok){
        throw new Error("Registration fail");
    }

    return response.json();
}

// login
export async function loginUser(username, password) {
    const response = await fetch(`${API_BASE}/login`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password })
    });

    if (!response.ok) {
        throw new Error("Login failed");
    }

    return response.json();
}


// check if user is logged in
export async function fetchCurrentUser() {
    const response = await fetch(`${API_BASE}/me`, {
        credentials: "include"
    });

    if (!response.ok) {
        return null;
    }

    return response.json();
}


// Logout user
export async function logoutUser() {
    const response = await fetch(`${API_BASE}/logout`, {
        method: "POST",
        credentials: "include"
    });

    if (!response.ok) {
        throw new Error("Logout failed");
    }
}