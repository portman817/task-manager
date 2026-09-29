import TASK_STATUSES from "../constants/taskStatuses.js"
function AddTask({onSubmit, title, description, taskStatus, setTitle, setDescription, setTaskStatus, onClose}){
    return(<form onSubmit={onSubmit}>
        <p>New Task: {title}</p>
        <label>Title</label>
        <input value={title} onChange={event => { setTitle(event.target.value)}}/>
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