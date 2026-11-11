import {useState} from "react"
import USER_ROLES from "../constants/userRoles.js"
function UserEditForm({userId, onEdit, onClose, userUsername, userRole, currentUserId, onLogout}){
    const [username, setUsername] = useState(userUsername)
    const [role, setRole] = useState(userRole)
    const handleSubmit = async (event)=>{
        event.preventDefault()
        if(username.trim()==="" && (role==="" || role===null)) return
        const userData = {username, role}
        onEdit(userId, userData)
        if (userId===currentUserId) onLogout()
        onClose()

    }
    return(
        <form onSubmit={handleSubmit}>
            <h3>Edit User</h3>
            <label>Username</label>
            <input value={username} onChange={event => (setUsername(event.target.value))}/>
            <label>Role</label>
            <select value={role} disabled={currentUserId===userId} onChange={event => (setRole(event.target.value))}>
                {Object.entries(USER_ROLES).map(([value, label])=><option key={value} value={value}>{label}</option>)}
            </select>
            <button type="subtim">Save</button>
            <button type="button" onClick={onClose}>Close</button>
        </form>
    )
}
export default UserEditForm