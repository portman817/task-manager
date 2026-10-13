import {registerUser} from "../api/authApi.js";
import {useState} from "react";

function RegisterForm({onClose}){
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const handleSubmit = async (event)=>{
        event.preventDefault()
        const success = await registerUser(username, password)
        if(!success) return
        console.log("Register is Successfully")
        onClose()
    }
    return(
        <form onSubmit={handleSubmit}>
            <label>Username</label>
            <input required type="text" value={username} onChange={event => (setUsername(event.target.value))}/>
            <label>Password</label>
            <input required type="password" value={password} onChange={event => (setPassword(event.target.value))}/><br/>
            <button type="submit" style={{marginRight: "15px"}}>Save</button>
            <button type="button" onClick={onClose}>Close</button>
        </form>
    )
}
export default RegisterForm