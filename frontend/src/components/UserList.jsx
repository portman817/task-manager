function UserList({users}){
    return(
        <ul>
            {users.map(user =>
                <li key={user.id} style={{marginBottom: "10px"}}>
                    <h3>Username: {user.username}</h3>
                    <p>Id: {user.id}</p>
                    <p>Role: {user.role}</p>
                    <p>Creating Date: {user.createdAt}</p>
                </li>)}
        </ul>
    )
}
export default UserList