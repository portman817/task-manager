import { useState } from "react";

function LoginForm({onLogin}) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const handleSubmit = async (event) => {
        event.preventDefault()
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({username,
                password})
        })
        if (!response.ok) {
            console.log("Login failed");
            return;
        }
        const data = await response.json()
        localStorage.setItem("token", data.token)
        onLogin()
    }

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label>Username</label>
                <input
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                />
            </div>

            <div>
                <label>Password</label>
                <input
                    type="password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />
            </div>

            <button type="submit">Login</button>
        </form>
    );
}

export default LoginForm;