import {useState} from "react";
function UsersList({users, onDelete, onClose, currentId}){
    const [selectedUserId, setSelectedUserId] = useState(null)
    const handleShowDetails = (id) => setSelectedUserId(id)
    const closeDetails = () => setSelectedUserId(null)
    return(
        <ul>
            {users.map(user =>
                <li key={user.id} style={{marginBottom: "10px"}}>
                    <h3>Username: {user.username}{currentId===user.id && "(Me)"}</h3>
                    <button onClick={()=>handleShowDetails(user.id)}>Details</button>
                    {selectedUserId===user.id && <><p>Id: {user.id}</p>
                        <p>Role: {user.role}</p>
                        <p>Creating Date: {user.createdAt}</p>
                        <button onClick={closeDetails}>Close details</button><br/></> }


                    <button onClick={()=>onDelete(user.id)} disabled={currentId===user.id} type="button">Delete</button>
                </li>)}
            <button onClick={onClose}>Close Users list</button>
        </ul>
    )
}
export default UsersList