import './App.css'
import {useEffect, useState} from 'react'
import LoginForm from "./components/LoginForm.jsx";
import Task from "./components/Task.jsx";
import AddTask from "./components/AddTask.jsx";
import {deleteTask, getTasks} from "./api/taskApi.js";

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
function Logout({onLogout}) {
    return(
        <button onClick={onLogout}>Logout</button>
    )
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

        const loadTasks = async ()=>{
            const data = await getTasks()
            if(data ===null) return

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
const handleDeleteTask = async (id)=>{
    const success = await deleteTask(id)
    if(!success) return
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
                  onDelete={()=>handleDeleteTask(task.taskId)} onEdit={()=>handleTaskEdit(task.taskId)} isEditing={isEditing(task.taskId)} onCloseEditing={closeEditingTask} onUpdate={updateTask}/>
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
