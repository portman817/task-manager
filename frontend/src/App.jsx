import './App.css'
import {useEffect, useState} from 'react'
import LoginForm from "./components/LoginForm.jsx";
import Task from "./components/Task.jsx";
import AddTask from "./components/AddTask.jsx";
import {deleteTask, getTasks, createTask} from "./api/taskApi.js";
import {getCurrentUser} from "./api/userApi.js";
import UpdateUsername from "./components/UpdateUsername.jsx"
import UpdatePassword from "./components/UpdatePassword.jsx";

function Header({appName}) {
   return(
       <h1>{appName}</h1>
   )
}
function Welcome({username, role, onUpdate, onLogout}) {
    const [showUpdateUsername, setShowUpdateUsername] = useState(false)
    const [showUpdatePassword, setShowUpdatePassword] = useState(false)
    const handleShowUpdateUsername = ()=>{

        setShowUpdateUsername(true)
    }
    const closeUpdateUsername = ()=>{
        setShowUpdateUsername(false)
    }
    const handleShowUpdatePassword =()=>{
        setShowUpdatePassword(true)
    }
    const closeUpdatePassword = ()=>{
        setShowUpdatePassword(false)
    }
    return(
        <>
            <h3>Welcome {username !== null ? username: ""}</h3>
            {username && (<><button onClick={handleShowUpdateUsername}>Edit username</button><br/>
            <button onClick={handleShowUpdatePassword}>Change password</button></>)}


            {showUpdateUsername &&

                <UpdateUsername currentUsername={username} onUpdate={onUpdate} onClose={closeUpdateUsername} onLogout={onLogout} />
             }
            {showUpdatePassword &&
            <UpdatePassword onClose={closeUpdatePassword}/>
            }
            {role && (
                <div>
                <p>Role {role}</p>
            </div>) }

        </>


)
}
function Logout({onLogout}) {
    return(
        <button onClick={onLogout}>Logout</button>
    )
}
function App() {
    const [loggedIn, setLoggedIn] = useState(localStorage.getItem("token") !== null);
    const [tasks, setTasks] = useState([])
    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [taskStatus, setTaskStatus] = useState("WARTET")
    const [showAddTask, setShowAddTask] = useState(false)
    const [editingTaskId, setEditingTaskId] = useState(null)
    const [currentUser, setCurrentUser]= useState(null)

    const handleLogout = ()=>{
        setLoggedIn(false)
        localStorage.removeItem("token")
        setTasks([])
        setEditingTaskId(null)
        setCurrentUser(null)
    }
    useEffect(() => {
        if(!loggedIn){
            return
        }

        const loadTasks = async ()=>{
            const result = await getTasks()
            if(result.status===401){
                handleLogout()
                return
            }
            if(!result.ok) return

            setTasks(result.data)
        }
        loadTasks()
        const loadCurrentUser = async ()=>{
            const result = await getCurrentUser()
            if(result.status===401){
                handleLogout()
                return
            }
            if(!result.ok) return
            setCurrentUser(result.data)
        }
        loadCurrentUser()

    }, [loggedIn])
const appName = "Task Manager";
const updateTask = (updatedTask)=>{
    setTasks(prev =>prev.map(task => task.taskId === updatedTask.taskId ? updatedTask: task))
}
const updateUsername =(updatedUser)=>{
    setCurrentUser(updatedUser)
    }
const handleLogin = () =>{

    setLoggedIn(true)
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
    const taskData = {title, description, taskStatus}
    const data = await createTask(taskData)
    if(data===null) return
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
          {loggedIn ? (<Welcome username = {currentUser !==null ? currentUser.username : ""} role={currentUser !==null ? currentUser.role: ""} onUpdate={updateUsername} onLogout={handleLogout}/>): "Sign in."}

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
              <p>{loggedIn && "We have no Tasks"}</p>
              )}
          {loggedIn && (<div><button onClick={handelShowAddTask}>New Task</button></div>)}

          {loggedIn && showAddTask && (<AddTask onSubmit={handleSubmit} title={title} description={description} taskStatus={taskStatus} setTitle={setTitle} setDescription={setDescription} setTaskStatus={setTaskStatus} onClose={closeAddTask}  />)}

      </>
  )
}
export default App
