const logIn = async (username, password)=>{
    const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({username,
            password})
    })
    if (!response.ok) {
        console.log("Login failed");
        return {
            ok: response.ok,
            status: response.status,
            data: null
        }
    }
    const data = await response.json()
    return {
        ok: response.ok,
        status: response.status,
        data: data
    }
}
export {logIn}