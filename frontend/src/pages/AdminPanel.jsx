import {getAllUsers} from "../api/adminApi.js";
import {useState} from "react";
import {Link} from "react-router-dom";

function AdminPanel(){
    const [users, setUsers] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")
    const getUsers = async ()=>{
        setLoading(true)
        try {
            setUsers([])
            setError("")
            const result = await getAllUsers()
            if (!result.ok) {
                setError("Error status:" + result.status)
                return
            }
            setUsers(result.data)
        }
        catch (error){
            console.error("Failed by Users upload", error)
            setError("Network error. Failed to load users.")
        }
        finally {
            setLoading(false)
        }
    }
    return(<>
        <h1>Admin Panel</h1>
            <button onClick={getUsers} disabled={loading}>Get All Users</button>
            {error && <p>{error}</p>}
            {users.length!==0 &&(
                <ul>
                    {users.map(user =>
                    <li key={user.id} style={{marginBottom: "10px"}}>
                        <h3>Username: {user.username}</h3>
                        <p>Id: {user.id}</p>
                        <p>Role: {user.role}</p>
                        <p>Creating Date: {user.createdAt}</p>
                    </li>)}
                </ul>
            )}
            <Link to="/">Back</Link>
        </>

    )
}
export default AdminPanel