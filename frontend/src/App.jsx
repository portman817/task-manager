import './App.css'
import {useEffect, useState} from 'react'
import LoginForm from "./LoginForm";

function Header({appName}) {
   return(
       <h1>{appName}</h1>
   )
}
function Welcome({username}) {
    return(
        <p>Welcome, {username}</p>
    )
}
function Task({task, onDelete, onEdit, isEditing, onCloseEditing, onUpdate}){
    return(
        <li ><button onClick={onEdit}>Edit</button>{task.title}
        <button onClick={onDelete}>Delete</button><br/>
            <p>{task.description}</p>
            <p>{task.status}</p>
            {isEditing && <EditingTask task={task} onClose={onCloseEditing} onUpdate={onUpdate}  />}
    </li>)
}
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
function Logout({onLogout}) {
    return(
        <button onClick={onLogout}>Logout</button>
    )
}
function AddTask({onSubmit, title, description, taskStatus, setTitle, setDescription, setTaskStatus, onClose}){
    return(<form onSubmit={onSubmit}>
        <p>New Task: {title}</p>
        <label>Title</label>
        <input value={title} onChange={event => { setTitle(event.target.value)}}/>
        <label>Description</label>
        <input value={description} onChange={event => {setDescription(event.target.value)}}/>
        <label>Task Status</label>
        <select value={taskStatus} onChange={event => {setTaskStatus(event.target.value)}}>
            <option value="WARTET">Wartet</option>
            <option value="IN_BEARBEITUNG">In Bearbeitung</option>
            <option value="FERTIG">Fertig</option>
        </select>
        <button type={"submit"}>Add Task</button>
        <div><button type="button" onClick={onClose}>Close add Tasks</button></div>
    </form>)
}

function App() {
    const [loggedIn, setLoggedIn] = useState(false);
    const [tasks, setTasks] = useState([])
    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [taskStatus, setTaskStatus] = useState("WARTET")
    const [showAddTask, setShowAddTask] = useState(false)
    const [editingTaskId, setEditingTaskId] = useState(null)
    useEffect(() => {
        if(!loggedIn){
            return
        }
        const token = localStorage.getItem("token")
        const loadTasks = async ()=>{
            const response = await fetch("http://localhost:8080/users/me/tasks",{
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                }

            })
            if(!response.ok){
                console.log("Failed to load tasks")
                return
            }
            const data = await response.json()
            setTasks(data)
        }
        loadTasks()

    }, [loggedIn])
const appName = "Task Manager";
const username = "Ivan";
const updateTask = (updatedTask)=>{
    setTasks(prev =>prev.map(task => task.taskId === updatedTask.taskId ? updatedTask: task))
}
const handleLogin = () =>{

    setLoggedIn(true)
}
const handleLogout = ()=>{
    setLoggedIn(false)
    localStorage.removeItem("token")
    setTasks([])
    setEditingTaskId(null)
}
const handleTaskEdit = (id)=>{
    setEditingTaskId(id)
}
const isEditing =(id)=>{
    return editingTaskId === id
}
const deleteTask = async (id)=>{
    const token = localStorage.getItem("token")
    const response = await fetch(`http://localhost:8080/users/me/tasks/${id}`,{
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${token}`}
    })
    if(!response.ok){
        console.log("Failed delete")
        return
    }
    setTasks(prev => prev.filter(task => task.taskId !==id))
}
const  handleSubmit = async (event)=>{
    event.preventDefault()
    if(title.trim()===""){
        return
    }
    const token = localStorage.getItem("token")
    const response = await fetch("http://localhost:8080/users/me/tasks", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({title, description, taskStatus})
    })
    if(!response.ok){
        return
    }
    const data = await response.json()
    setTasks(prev=>[...prev, data])
    setTitle("")
    setDescription("")
    setTaskStatus("WARTET")
}
const handelShowAddTask = ()=>{
    setShowAddTask(true)
}
const closeAddTask = ()=>{
    setShowAddTask(false)
}
const closeEditingTask = ()=>{
    setEditingTaskId(null)
}
  return (
      <>
          <Header appName={appName} />
        <Welcome username = {username} />
          {!loggedIn ? <LoginForm onLogin={handleLogin}  /> : <Logout onLogout={handleLogout}/>}

          <p>{loggedIn ? "Logged in" : "Not Logged in"}</p>
          {tasks.length!==0 && loggedIn ?(
          <ul>
              { tasks.map(task =>
                  <Task
                  key={task.taskId}
                  task={task}
                  onDelete={()=>deleteTask(task.taskId)} onEdit={()=>handleTaskEdit(task.taskId)} isEditing={isEditing(task.taskId)} onCloseEditing={closeEditingTask} onUpdate={updateTask}/>
              )}
          </ul>) :(
              <p>{loggedIn ? "We have no Tasks": ""}</p>
              )}
          {loggedIn && (<div><button onClick={handelShowAddTask}>New Task</button></div>)}

          {loggedIn && showAddTask && (<AddTask onSubmit={handleSubmit} title={title} description={description} taskStatus={taskStatus} setTitle={setTitle} setDescription={setDescription} setTaskStatus={setTaskStatus} onClose={closeAddTask}  />)}

      </>
  )
}
export default App
