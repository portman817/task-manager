import EditingTask from "./EditingTask.jsx";
import {useState} from "react";

function Task({task, onDelete, onUpdate, currentUserRole, availableUsers}){
    const [editingTaskId, setEditingTaskId] = useState(null)
    const showEditingTask = (id) => setEditingTaskId(id)
    const closeEditingTask = () => setEditingTaskId(null)
    return(
        <li ><button onClick={()=> showEditingTask(task.taskId)}>Edit</button>{task.title}
            <button onClick={()=>onDelete(task.taskId)}>Delete</button><br/>
            {currentUserRole==="Admin" && <p>{task.taskId}</p>}
            <p>{task.description}</p>
            <p>{task.status}</p>
            {editingTaskId && <EditingTask task={task} onClose={closeEditingTask} onUpdate={onUpdate} availableUsers={availableUsers} currentUserRole={currentUserRole} />}
        </li>)
}

export default Task;