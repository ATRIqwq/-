import {userType} from '../module/user'

let currentUser: userType;
const setCurrentUserState = (user:userType)=>{
    currentUser = user;
}

const getCurrentUserState = ()=>{
   return currentUser;
}

export {
    setCurrentUserState,
    getCurrentUserState,
}
