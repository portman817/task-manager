const getCurrentUser = async ()=>{
    const token = localStorage.getItem("token")
    const response = await fetch("http://localhost:8080/users/me", {
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`}
        })
    if(!response.ok) {
        console.log(`ERROR_STATUS: ${response.status}`)
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
const currentUserUpdateUsername = async (username)=>{
    const token = localStorage.getItem("token")
    const response = await fetch("http://localhost:8080/users/me", {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({username})
    })
    if(!response.ok){
        return{
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
const currentUserChangePassword = async (oldPassword, newPassword)=>{
    const token = localStorage.getItem("token")
    const response = await fetch("http://localhost:8080/users/me/password",{
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({oldPassword, newPassword})
    })
    if(!response.ok) {
        console.log("Password change failed")
        return false
    }

        return true
    }

export {getCurrentUser, currentUserUpdateUsername, currentUserChangePassword}