import { useState } from "react";
import {logIn} from "../api/authApi.js"

function LoginForm({onLogin}) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const handleSubmit = async (event) => {
        event.preventDefault()
        const result = await logIn(username, password)
        if(!result.ok) return
        localStorage.setItem("token", result.data.token)
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