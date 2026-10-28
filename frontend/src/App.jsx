import './App.css'
import {useEffect, useState} from 'react'
import LoginForm from "./components/LoginForm.jsx";
import Task from "./components/Task.jsx";
import AddTask from "./components/AddTask.jsx";
import {deleteTask, getTasks, createTask} from "./api/taskApi.js";
import {getCurrentUser} from "./api/userApi.js";
import RegisterForm from "./components/RegisterForm.jsx";
import Welcome from "./components/Welcome.jsx";
import {Routes, Route, Navigate, Link} from "react-router-dom";
import AdminPanel from "./pages/AdminPanel.jsx";

function Header({appName}) {
   return(
       <h1>{appName}</h1>
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
    const [isRegistered, setIsRegistered] = useState(true)
    const [userLoading, setUserLoading] = useState(loggedIn)

    const handleLogout = ()=>{
        setLoggedIn(false)
        localStorage.removeItem("token")
        setTasks([])
        setEditingTaskId(null)
        setCurrentUser(null)
        setUserLoading(false)
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
            try {
                const result = await getCurrentUser()
                if(result.status===401){
                    handleLogout()
                    return
                }
                if(!result.ok) return
                setCurrentUser(result.data)

            }
            catch (error){
                console.log("Failed to load current user:", error)
            }
            finally {
                setUserLoading(false)
            }

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
    setUserLoading(true)
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
const showRegisterForm= ()=>{
    setIsRegistered(false)
    }
    const closeRegisterFOrm =()=>{
    setIsRegistered(true)
    }
  return (
      <Routes>
          <Route path="/" element={
                              <>
                                  <Header appName={appName} />
                                  {loggedIn && currentUser ? (<Welcome username = {currentUser.username} role={currentUser.role} onUpdate={updateUsername} onLogout={handleLogout}/>): "Sign in."}
                                  {loggedIn && currentUser?.role==="ADMIN" && <Link to="/admin">Open Admin Panel</Link>
                                  }
                                  {!loggedIn && isRegistered && <><LoginForm onLogin={handleLogin}  /><br/>
                                      <button type="button" onClick={showRegisterForm}>Register</button>
                                  </> }
                                  {!loggedIn && !isRegistered && <RegisterForm onClose={closeRegisterFOrm} />}
                                  {loggedIn && <Logout onLogout={handleLogout}/>}
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

          }/>
          <Route path="/admin" element={
            userLoading ? <p>User Loading</p>: loggedIn && currentUser.role==="ADMIN" ? <AdminPanel /> : <Navigate to="/" replace />

                               }/>
      </Routes>

  )
}
export default App
