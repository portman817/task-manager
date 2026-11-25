import './App.css'
import {useEffect, useState} from 'react'
import LoginForm from "./components/LoginForm.jsx";
import Task from "./components/Task.jsx";
import AddTask from "./components/AddTask.jsx";
import {deleteTask, getTasks, createTask, editTask} from "./api/taskApi.js";
import {getCurrentUser} from "./api/userApi.js";
import RegisterForm from "./components/RegisterForm.jsx";
import Welcome from "./components/Welcome.jsx";
import {Routes, Route, Navigate, Link, useLocation} from "react-router-dom";
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
    const location = useLocation()
    const [loggedIn, setLoggedIn] = useState(localStorage.getItem("token") !== null);
    const [tasks, setTasks] = useState([])
    const [showAddTask, setShowAddTask] = useState(false)
    const [currentUser, setCurrentUser]= useState(null)
    const [isRegistered, setIsRegistered] = useState(true)
    const [userLoading, setUserLoading] = useState(loggedIn)

    const handleLogout = ()=>{
        setLoggedIn(false)
        localStorage.removeItem("token")
        setTasks([])
        setCurrentUser(null)
        setUserLoading(false)
    }
    useEffect(() => {
        if(!loggedIn){
            return
        }
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
    useEffect(() => {
        if (!loggedIn || location.pathname !== "/") return
        const loadTasks = async () => {
            try {
                const result = await getTasks()

                if (result.status === 401) {
                    handleLogout()
                    return
                }

                if (!result.ok) return

                setTasks(result.data)

            } catch (error) {
                console.error("Failed to load tasks:", error)
            }
        }
        loadTasks()

    }, [loggedIn, location.pathname])
const appName = "Task Manager";
const updateTask = async (updatedTask, id)=>{
    const result = await editTask(updatedTask, id)
    if(result.status===401){
        handleLogout()
        return
    }
    if(!result.ok) return
    setTasks(prev =>prev.map(task => task.taskId === result.data.taskId ? result.data: task))
}
const updateUsername =(updatedUser)=>{
    setCurrentUser(updatedUser)
    }
const handleLogin = () =>{

    setLoggedIn(true)
    setUserLoading(true)
}
const handleDeleteTask = async (id)=>{
    const success = await deleteTask(id)
    if(!success) return
    setTasks(prev => prev.filter(task => task.taskId !==id))
}
const  handleSubmit = async (task)=>{
try {
    const result = await createTask(task)
    if(result.status === 401){
        handleLogout()
        return false
    }
    if (!result.ok) return false
    setTasks(prev=>[...prev, result.data])
    return true
}
catch (error){
    console.error("Task creating is failed", error)
    return false
}

}
const handelShowAddTask = ()=>{
    setShowAddTask(true)
}
const closeAddTask = ()=>{
    setShowAddTask(false)
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
                                                  onDelete={handleDeleteTask}   onUpdate={updateTask}/>
                                          )}
                                      </ul>) :(
                                      <p>{loggedIn && "We have no Tasks"}</p>
                                  )}
                                  {loggedIn && (<div><button onClick={handelShowAddTask}>New Task</button></div>)}

                                  {loggedIn && showAddTask && (<AddTask onSubmit={handleSubmit} onClose={closeAddTask}  />)}

                              </>

          }/>
          <Route path="/admin" element={userLoading ? <p>User Loading</p>: loggedIn && currentUser?.role==="ADMIN" ? <AdminPanel currentUserId={currentUser.id} onLogout={handleLogout} currentUserRole={currentUser.role} /> : <Navigate to="/" replace />}/>
      </Routes>

  )
}
export default App
