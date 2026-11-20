import {useState} from "react";
import UserEditForm from "./UserEditForm.jsx";
import Task from "./Task.jsx";
import PasswordChangeForm from "./PasswordChangeForm.jsx";
import AddTask from "./AddTask.jsx";
function UsersList({users, onDelete, onClose, currentId, onEdit, onChangePassword, onLogout, onShowTasksByUserId, onCreateTask, onDeleteTask, currentUserRole}){
    const [selectedUserId, setSelectedUserId] = useState(null)
    const [selectEditingUserId, setSelectEditingUserId] = useState(null)
    const [selectChangePasswordUserId, setSelectChangePasswordUserId] = useState(null)
    const [selectShowTasksUserId, setSelectShowTasksUserId] = useState(null)
    const [tasks, setTasks] = useState([])
    const [selectCreateTaskUserId, setSelectCreateTaskUserId] = useState(null)
    const closeDetails = () => setSelectedUserId(null)
    const handleSHowEdit = (id)=> setSelectEditingUserId(id)
    const closeEdit = ()=> setSelectEditingUserId(null)
    const handleShowChangePassword = (id)=> setSelectChangePasswordUserId(id)
    const closeChangePassword = ()=> setSelectChangePasswordUserId(null)
    const handleShowTasksByUserId = async (id)=>{
        setSelectShowTasksUserId(null)
        setTasks([])
        const data = await onShowTasksByUserId(id)
        if (!Array.isArray(data)) return
        setTasks(data)
        setSelectShowTasksUserId(id)
    }
    const closeShowTasksByUserId = ()=> setSelectShowTasksUserId(null)
    const showCreateTask = (id)=> setSelectCreateTaskUserId(id)
    const closeCreateTask = ()=> setSelectCreateTaskUserId(null)
    const handleShowDetails = (id) =>{
        closeShowTasksByUserId()
        setSelectedUserId(id)
    }
    const createTask = async (taskData)=>{
        const data = await onCreateTask(taskData)
        if (!data || typeof data !== "object") return false
        setTasks(prev => [...prev, data])
        return true
    }
    const handleDeleteTask = async (id)=>{
        const success = await onDeleteTask(id)
        if(!success) return
        setTasks(prev => prev.filter(task => task.taskId !==id))
    }
    return(
        <ul>
            {users.map(user =>
                <li key={user.id} style={{marginBottom: "10px"}}>
                    <h3>Username: {user.username}{currentId===user.id && "(Me)"}</h3>
                    <button onClick={()=>handleShowDetails(user.id)}>Details</button>
                    {selectedUserId===user.id && <><p>Id: {user.id}</p>
                        <p>Role: {user.role}</p>
                        <p>Creating Date: {user.createdAt}</p><br/>
                        <button onClick={() =>handleShowTasksByUserId(user.id)}>Show tasks</button>
                        {selectShowTasksUserId && (
                            <>
                                {tasks.length !== 0 ? (<ul>{tasks.map(task => (<Task key={task.taskId} task={task} onDelete={handleDeleteTask} currentUserRole={currentUserRole} />))}</ul>) : (<p>This user has no tasks.</p>)}
                                <button onClick={() => showCreateTask(user.id)}>Create Task</button>
                                {selectCreateTaskUserId && <AddTask onSubmit={createTask} userId={currentUserRole==="ADMIN" ? user.id: null} onClose={closeCreateTask} />}
                                <button onClick={closeShowTasksByUserId}>Close tasks</button>
                            </>
                        )}
                        <button onClick={closeDetails}>Close details</button><br/></> }

                    <button onClick={()=>handleSHowEdit(user.id)}>Edit user</button>
                    {selectEditingUserId===user.id && <UserEditForm onEdit={onEdit} userId={user.id} onClose={closeEdit} userUsername={user.username} userRole={user.role} currentUserId={currentId} onLogout={onLogout} />}
                    <button onClick={() =>handleShowChangePassword(user.id)}>Change password</button>
                    {selectChangePasswordUserId===user.id && <PasswordChangeForm userId={user.id} onPasswordChange={onChangePassword} onClose={closeChangePassword}/>}
                    <button onClick={()=>onDelete(user.id)} disabled={currentId===user.id} type="button">Delete</button>
                </li>)}
            <button onClick={onClose}>Close Users list</button>
        </ul>
    )
}
export default UsersList