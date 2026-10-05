import {currentUserUpdateUsername} from "../api/userApi.js"
import {useState} from "react"

function UpdateUsername({currentUsername, onUpdate, onClose, onLogout}) {
    const [newUsername, setNewUserName] = useState("")
    const handleSubmit = async (event) => {
      event.preventDefault()
        if (currentUsername === newUsername) return
        const result = await currentUserUpdateUsername(newUsername)
        if(!result.ok) return
        onUpdate(result.data)
        onLogout()



    }
    return(
        <form onSubmit={handleSubmit}>
            <label>New username</label>
            <input value={newUsername} onChange={event=>{setNewUserName(event.target.value)}}/><br/>
            <button type="submit">Save</button><br/>
            <button type="button" onClick={onClose}>Close</button>
        </form>)
}
export default UpdateUsername