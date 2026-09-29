import {useState} from "react";
import {editTask} from "../api/taskApi.js";
import TASK_STATUSES from "../constants/taskStatuses.js"

function EditingTask({task, onClose, onUpdate}){
    const [editTitle, setEditTitle] = useState(task.title)
    const [editDescription, setEditDescription] = useState(task.description)
    const [editTaskStatus, setEditTaskStatus] = useState(task.status)
    const handleSubmit = async (event)=>{
        event.preventDefault()
        const updatedTaskFields = {}
        if(editTitle !== task.title) updatedTaskFields.title=editTitle
        if(editDescription !== task.description) updatedTaskFields.description=editDescription
        if(editTaskStatus !== task.status) updatedTaskFields.taskStatus=editTaskStatus
        if(Object.keys(updatedTaskFields).length===0) return
        const data = await editTask(updatedTaskFields, task.taskId)
        if(data===null) return
        onUpdate(data)
        onClose()
    }
    return(<form onSubmit={handleSubmit} style={{border: "1px white solid"}}>
        <label>Title</label><br/>
        <input value={editTitle} onChange={event => {setEditTitle(event.target.value)}}/><br/>
        <label>Description</label><br/>
        <input value={editDescription} onChange={event => {setEditDescription(event.target.value)}}/><br/>
        <label>Task Status</label><br/>
        <select style={{marginBottom: "20px", padding: "5px"}} value={editTaskStatus} onChange={event => {setEditTaskStatus(event.target.value)}}>
            {Object.entries(TASK_STATUSES).map(([value, label]) =><option key={value} value={value}>{label}</option>)}
        </select><br/>
        <button style={{marginRight: "10px"}} type="submit">Save</button>
        <button type="button" onClick={onClose}>Close Editing</button>
    </form>)
}

export default EditingTask;