import TASK_STATUSES from "../constants/taskStatuses.js"
import {useState} from "react";

function AddTask({onSubmit, onClose}){
    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [taskStatus, setTaskStatus] = useState("WARTET")
    const handleCreateTask = async (event)=>{
        event.preventDefault()
        if(title.trim()==="") return
        const taskData = {title, description, taskStatus}
        const success = await onSubmit(taskData)
        if(!success) return
        setTitle("")
        setDescription("")
        setTaskStatus("WARTET")
    }
    return(<form onSubmit={handleCreateTask}>
        <p>New Task: {title}</p>
        <label>Title</label>
        <input required value={title} onChange={event => { setTitle(event.target.value)}}/>
        <label>Description</label>
        <input value={description} onChange={event => {setDescription(event.target.value)}}/>
        <label>Task Status</label>
        <select value={taskStatus} onChange={event => {setTaskStatus(event.target.value)}}>
            {Object.entries(TASK_STATUSES).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
        </select>
        <button type={"submit"}>Add Task</button>
        <div><button type="button" onClick={onClose}>Close add Tasks</button></div>
    </form>)
}

export default AddTask;