import {useState} from "react";
import UpdatePassword from "./UpdatePassword.jsx";
import UpdateUsername from "./UpdateUsername.jsx";

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
export default Welcome