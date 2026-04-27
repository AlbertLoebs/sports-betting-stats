import { useState } from "react";
import { loginUser, registerUser } from "../services/AuthApi";
import './LoginPage.css';

function LoginPage( {setCurrentUser} ){
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    // controls whether we are in login mode or register mode
    const [isRegistering, setIsRegistering] = useState(false);
    const [error, setError] = useState("");

    // runs on form submit
    async function handleSubmit(e) {
        e.preventDefault(); // prevents page refresh
        setError(""); // clear old errors

        try {
            let user;

            // calls login or register
            if (isRegistering){
                user = await registerUser(username,password)
            } else {
                user = await loginUser(username, password)
            }

            // user is now logged in
            setCurrentUser(user)
        } catch (err) {
            // show msg
            setError(isRegistering ? "Registration failed" : "Login failed")
        }
    }

    return (
        <div className="login-page">
            {/* title will change based on mode */}
            <h1>{isRegistering ? "Create Account" : "Login"}</h1>

            {/* Form for username and password */}
            <form onSubmit={handleSubmit}>
                {/* username input */}

                <div>
                    <label>Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>

                {/* Password input */}
                <div>
                    <label>Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>

                {/* submit button */}
                <button type="submit">
                    {isRegistering ? "Register" : "Login"}
                </button>
            </form>

            {/* error if anything fails */}
            {error && <p>{error}</p>}

            {/* Toggle between login and register mode */}
            <button onClick={() => setIsRegistering(!isRegistering)}>
                {isRegistering
                    ? "Already have an account? Login"
                    : "Need an account? Register"}
            </button>

        </div>
    );
}

export default LoginPage