import {getAllUsers} from "../api/adminApi.js";
import {useState} from "react";
import {Link} from "react-router-dom";

function AdminPanel(){
    const [users, setUsers] = useState([])
    const getUsers = async ()=>{
        const result = await getAllUsers()
        if (!result.ok) return
        setUsers(result.data)
    }
    return(<>
        <h1>Admin Panel</h1>
            <button onClick={getUsers}>Get All Users</button>
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