import {getAllUsers, deleteUser, createUser} from "../api/adminApi.js";
import {useState} from "react";
import {Link} from "react-router-dom";
import UsersList from "../components/UsersList.jsx";
import CreateUserForm from "../components/CreateUserForm.jsx"

function AdminPanel({currentUserId, onLogout}){
    const [users, setUsers] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")
    const [showCreateUser, setShowCreateUser] = useState(false)
    const getUsers = async ()=>{
        setLoading(true)
        try {
            setUsers([])
            setError("")
            const result = await getAllUsers()
            if(result.status===401) {
                onLogout()
                return
            }
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
            const result = await deleteUser(id)
            if(result.status===401){
                onLogout()
                return
            }
            if(!result.ok) return

            setUsers(prev =>prev.filter(user =>user.id !== id))
            console.log("User deleted successefully")
        }
        catch (error){
            console.error("User delete failed", error)
        }

    }
    const handleCreateUser = async (user) => {
        try {
            const result = await createUser(user)
            if(result.status===401){
                onLogout()
                return false
            }
            if(!result.ok) return
            if(users.length>0){
                setUsers(prev =>[...prev, result.data])
            }
            return true

        }
        catch(error){
            console.error("Failed to create user:", error)
            return false
        }


    }
    const closeUsersList =()=>
        setUsers([])
    const handleShowCreateUser = ()=>
        setShowCreateUser(true)
    const closeCreateUser = ()=>
        setShowCreateUser(false)


    return(<>
        <h1>Admin Panel</h1>
            <button onClick={getUsers} disabled={loading}>Get All Users</button>
            {error && <p>{error}</p>}
            {users.length!==0 && <UsersList currentId={currentUserId} users={users} onDelete={handleDeleteUser} onClose={closeUsersList} />}
            <button onClick={handleShowCreateUser} style={{marginTop: "10px"}}>Create User</button>
            {showCreateUser && <CreateUserForm onSubmit={handleCreateUser} onClose={closeCreateUser} />}
            <Link to="/">Back</Link>
        </>

    )
}
export default AdminPanel