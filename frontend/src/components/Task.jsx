import EditingTask from "./EditingTask.jsx";

function Task({task, onDelete, onEdit, isEditing, onCloseEditing, onUpdate, currentUserRole}){
    return(
        <li ><button onClick={onEdit}>Edit</button>{task.title}
            <button onClick={()=>onDelete(task.taskId)}>Delete</button><br/>
            {currentUserRole==="Admin" && <p>{task.taskId}</p>}
            <p>{task.description}</p>
            <p>{task.status}</p>
            {isEditing && <EditingTask task={task} onClose={onCloseEditing} onUpdate={onUpdate}  />}
        </li>)
}

export default Task;