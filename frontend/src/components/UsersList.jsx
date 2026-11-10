import {useState} from "react";
import UserEditForm from "./UserEditForm.jsx"
import PasswordChangeForm from "./PasswordChangeForm.jsx";
function UsersList({users, onDelete, onClose, currentId, onEdit, onChangePassword}){
    const [selectedUserId, setSelectedUserId] = useState(null)
    const [selectEditingUserId, setSelectEditingUserId] = useState(null)
    const [selectChangePasswordUserId, setSelectChangePasswordUserId] = useState(null)
    const handleShowDetails = (id) => setSelectedUserId(id)
    const closeDetails = () => setSelectedUserId(null)
    const handleSHowEdit = (id)=> setSelectEditingUserId(id)
    const closeEdit = ()=> setSelectEditingUserId(null)
    const handleShowChangePassword = (id)=> setSelectChangePasswordUserId(id)
    const closeChangePassword = ()=> setSelectChangePasswordUserId(null)
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

                    <button onClick={()=>handleSHowEdit(user.id)}>Edit user</button>
                    {selectEditingUserId===user.id && <UserEditForm onEdit={onEdit} userId={user.id} onClose={closeEdit} />}
                    <button onClick={() =>handleShowChangePassword(user.id)}>Change password</button>
                    {selectChangePasswordUserId===user.id && <PasswordChangeForm userId={user.id} onPasswordChange={onChangePassword} onClose={closeChangePassword}/>}
                    <button onClick={()=>onDelete(user.id)} disabled={currentId===user.id} type="button">Delete</button>
                </li>)}
            <button onClick={onClose}>Close Users list</button>
        </ul>
    )
}
export default UsersList