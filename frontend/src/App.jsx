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
function Task({task, onDelete}){
    return(
        <li >{task.title}
        <button onClick={onDelete}>Delete</button><br/>
            <p>{task.description}</p>
    </li>)
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
        <input value={taskStatus} onChange={event => {setTaskStatus(event.target.value)}}/>
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

const handleLogin = () =>{

    setLoggedIn(true)
}
const handleLogout = ()=>{
    setLoggedIn(false)
    localStorage.removeItem("token")
    setTasks([])
}
const deleteTask = async (id)=>{
    console.log("DELETE ID:", id)
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
                  onDelete={()=>deleteTask(task.taskId)}/>
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
