import {useState} from "react";

function PasswordChangeForm({userId, onPasswordChange, onClose}){
    const [newPassword, setNewPassword] = useState("")
    const handleSubmit = async (event)=>{
        event.preventDefault()
        if(newPassword.trim()==="") return
        const success = onPasswordChange(userId, newPassword)
        if (!success) {
            console.error("Change password failed")
            return
        }
        console.log("Password changed successfully")
        onClose()
    }
    return(
        <form onSubmit={handleSubmit}>
            <label>New password</label>
            <input value={newPassword} onChange={event => (setNewPassword(event.target.value))} type="password" required/>
            <button type="submit">Save</button>
            <button type="button" onClick={onClose}>Close</button>
        </form>
    )
}
export default PasswordChangeForm