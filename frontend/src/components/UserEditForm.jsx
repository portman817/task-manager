import {useState} from "react"
import USER_ROLES from "../constants/userRoles.js"
function UserEditForm({userId, onEdit, onClose}){
    const [username, setUsername] = useState("")
    const [role, setRole] = useState("USER")
    const handleSubmit = async (event)=>{
        event.preventDefault()
        if(username.trim()==="") return
        const userData = {username, role}
        onEdit(userId, userData)

    }
    return(
        <form onSubmit={handleSubmit}>
            <h3>Edit User</h3>
            <label>Username</label>
            <input value={username} onChange={event => (setUsername(event.target.value))}/>
            <label>Role</label>
            <select value={role} onChange={event => (setRole(event.target.value))}>
                {Object.entries(USER_ROLES).map(([value, label])=><option key={value} value={value}>{label}</option>)}
            </select>
            <button type="subtim">Save</button>
            <button type="button" onClick={onClose}>Close</button>
        </form>
    )
}
export default UserEditForm