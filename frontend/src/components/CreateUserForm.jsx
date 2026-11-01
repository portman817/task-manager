import {useState} from "react"
import USER_ROLES from "../constants/userRoles.js"
function CreateUserForm({onSubmit, onClose}) {
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [role, setRole] = useState("USER")
    const handleSubmit = async (event)=>{
        event.preventDefault()
        if(username.trim()==="" || password.trim()==="") return
        const userData = {username, password, role}
        const success = await onSubmit(userData)
        if(!success) return
        setUsername("")
        setPassword("")
        setRole("USER")
        onClose()
    }
    return(
        <form onSubmit={handleSubmit}>
            <p>New User {username}</p>
            <label>Username</label>
            <input value={username} onChange={event => (setUsername(event.target.value))} required/>
            <label>Password</label>
            <input value={password} onChange={event => (setPassword(event.target.value))} required type="password"/>
            <label>Role</label>
            <select value={role} onChange={event => (setRole(event.target.value))}>
                {Object.entries(USER_ROLES).map(([value, label])=><option key={value} value={value}>{label}</option>)}
            </select>
            <button type="submit">Save</button>
            <button type="button" onClick={onClose}>Close</button>
        </form>
    )

}
export default CreateUserForm