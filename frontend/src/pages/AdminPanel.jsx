import {getAllUsers, deleteUser} from "../api/adminApi.js";
import {useState} from "react";
import {Link} from "react-router-dom";
import UsersList from "../components/UsersList.jsx";

function AdminPanel({currentUserId}){
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
    const handleDeleteUser = async(id)=>{
        try {
            const success = await deleteUser(id)
            if(!success) return
            setUsers(prev =>prev.filter(user =>user.id !== id))
            console.log("User deleted successefully")
        }
        catch (error){
            console.error("User delete failed", error)
        }

    }
    const closeUsersList =()=>
        setUsers([])


    return(<>
        <h1>Admin Panel</h1>
            <button onClick={getUsers} disabled={loading}>Get All Users</button>
            {error && <p>{error}</p>}
            {users.length!==0 && <UsersList currentId={currentUserId} users={users} onDelete={handleDeleteUser} onClose={closeUsersList} />}
            <Link to="/">Back</Link>
        </>

    )
}
export default AdminPanel