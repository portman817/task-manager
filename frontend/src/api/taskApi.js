const deleteTask = async (id)=>{
    const token = localStorage.getItem("token")
    const response = await fetch(`http://localhost:8080/users/me/tasks/${id}`,{
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${token}`}
    })
    if(!response.ok){
        console.log("Failed delete")
        return false
    }
    return true
}
const getTasks = async ()=>{
    const token = localStorage.getItem("token")
    const response = await fetch("http://localhost:8080/users/me/tasks",{
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }

    })
    if(!response.ok){
        console.log("Failed to load tasks")
        return null
    }
    const data = await response.json()
    return data

}
export {deleteTask, getTasks}