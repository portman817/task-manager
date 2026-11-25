const getAllUsers = async ()=>{
    const token = localStorage.getItem("token")
    const response = await fetch("http://localhost:8080/admin/users", {
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    })
    if(!response.ok){
        return{
            ok: response.ok,
            status: response.status,
            data: null
        }
    }
    const data = await response.json()
    return {
        ok: response.ok,
        status: response.status,
        data: data
    }
}
const deleteUser = async (id) =>{
    const token = localStorage.getItem("token")
    const response = await fetch(`http://localhost:8080/admin/users/${id}`,{
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${token}`
        }
    })
    if(!response.ok){
        console.log("User delete failed")
        return {
            ok: response.ok,
            status: response.status,
            data: null
        }
    }
    return {
        ok: response.ok,
        status: response.status,
        data: null
    }
}
const createUser = async (user) =>{
    const token = localStorage.getItem("token")
    const response = await fetch("http://localhost:8080/admin/users", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(user)
    })
    if(!response.ok){
        console.error("User created failed")
        return {
            ok: response.ok,
            status: response.status,
            data: null
        }

    }
    const data = await response.json()
    return {
        ok: response.ok,
        status: response.status,
        data: data
    }
}
const updateUser = async (id, userData)=>{
    const token = localStorage.getItem("token")
    const response = await fetch(`http://localhost:8080/admin/users/${id}`,{
        method: "PATCH",
        headers: {
            "Content-type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(userData)
    })
    if(!response.ok){
        console.error("Update user failed")
        return{
            ok: response.ok,
            status: response.status,
            data: null
        }
    }
    const data = await response.json()
    return {
        ok: response.ok,
        status: response.status,
        data: data
    }
}
const changePassword = async (id, newPassword)=>{
    const token = localStorage.getItem("token")
    const response = await fetch(`http://localhost:8080/admin/users/${id}/password`, {
        method: "PUT",
        headers: {
            "Content-type": "application/json",
            Authorization: `Bearer ${token}`,

        },
        body: JSON.stringify({newPassword})

    })
    if(!response.ok){
        console.error("Change password failed")
        return{
            ok: response.ok,
            status: response.status,
            data: null
        }
    }
    return {
        ok: response.ok,
        status: response.status,
        data: null
    }
}
const getTasksByUserId = async (userId) =>{
    const token = localStorage.getItem("token")
    const responses = await fetch(`http://localhost:8080/admin/users/${userId}/tasks`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    })
    if(!responses.ok){
        return{
            ok: responses.ok,
            status: responses.status,
            data: null
        }
    }
    const data = await responses.json()
    return {
        ok: responses.ok,
        status: responses.status,
        data: data
    }
}
const createTask = async (taskData)=>{
    const token = localStorage.getItem("token")
    const response = await fetch(`http://localhost:8080/admin/tasks`, {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(taskData)
    })
    if(!response.ok) {
        console.error("Create task failed")
        return{
            ok: response.ok,
            status: response.status,
            data: null
        }
    }
    const data = await response.json()
    return {
        ok: response.ok,
        status: response.status,
        data: data
    }

}
const deleteTask = async (id)=>{
    const token = localStorage.getItem("token")
    const response = await fetch(`http://localhost:8080/admin/tasks/${id}`, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${token}`
        }
    })
    if(!response.ok){
        console.log("Delete task failed")
        return{
            ok: response.ok,
            status: response.status,
            data: null
        }
    }
    return {
        ok: response.ok,
        status: response.status,
        data: null
    }
}
const editTask = async (taskData, id)=>{
    console.log("Task ID:", id)
    console.log("Request body:", JSON.stringify(taskData))
    const token = localStorage.getItem("token")
    const response = await fetch(`http://localhost:8080/admin/tasks/${id}`, {
        method: "PATCH",
        headers: {
            "Content-type": "application/json",
            Authorization: `Bearer ${token}`

        },
        body: JSON.stringify(taskData)
    })
    if(!response.ok){
        console.error("Update task failed:", response.status)
        console.error("Backend response:", await response.text())
        return{
            ok: response.ok,
            status: response.status,
            data: null
        }
    }
    const data = await response.json()
    return {
        ok: response.ok,
        status: response.status,
        data: data
    }
}
export {getAllUsers, deleteUser, createUser, updateUser, changePassword, getTasksByUserId, createTask, deleteTask, editTask}