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
export {getAllUsers, deleteUser}