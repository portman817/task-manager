import {currentUserChangePassword} from "../api/userApi.js";
import {useState} from "react";

function UpdatePassword({onClose}) {
    const [oldPassword, setOldPassword] = useState("")
    const [newPassword, setNewPassword] = useState("")
    const handleSubmit = async (event)=>{
        event.preventDefault()
        if(oldPassword === newPassword) {
            alert("The old and new passwords must be different.")
            return
        }
        const success = await currentUserChangePassword(oldPassword, newPassword)
        if(!success) {
            alert("Password change failed")
            return
        }
        alert("PASSWORD CHANGED")
        console.log("PASSWORD CHANGED - CLOSING")
        onClose()
    }
    return(
        <form onSubmit={handleSubmit}>
            <label>Old password</label>
            <input value={oldPassword} onChange={event => (setOldPassword(event.target.value))}/>
            <label>New password</label>
            <input value={newPassword} onChange={event => (setNewPassword(event.target.value))}/>
            <button type="submit">Save</button>
            <button type="button" onClick={onClose}>Close</button>
        </form>
    )
}
export default UpdatePassword