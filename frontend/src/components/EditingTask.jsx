import {useState} from "react";

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
        const token = localStorage.getItem("token")
        const response = await fetch(`http://localhost:8080/users/me/tasks/${task.taskId}`,{
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`
            },
            body: JSON.stringify(updatedTaskFields)
        })
        if(!response.ok) return console.log("Updating task failed")
        const data = await response.json()
        onUpdate(data)
    }
    return(<form onSubmit={handleSubmit} style={{border: "1px white solid"}}>
        <label>Title</label><br/>
        <input value={editTitle} onChange={event => {setEditTitle(event.target.value)}}/><br/>
        <label>Description</label><br/>
        <input value={editDescription} onChange={event => {setEditDescription(event.target.value)}}/><br/>
        <label>Task Status</label><br/>
        <select style={{marginBottom: "20px"}} value={editTaskStatus} onChange={event => {setEditTaskStatus(event.target.value)}}>
            <option value="WARTET">Wartet</option>
            <option value="IN_BEARBEITUNG">In Bearbeitung</option>
            <option value="FERTIG">Fertig</option>
        </select><br/>
        <button style={{marginRight: "10px"}} type="submit">Save</button>
        <button type="button" onClick={onClose}>Close Editing</button>
    </form>)
}

export default EditingTask;