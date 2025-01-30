import type { UserType } from '../models/user.d.ts';

let currentUser: UserType;
const setCurrentUserState = (user: UserType) =>{
    currentUser = user;
}

const getCurrentUserState = () : UserType=> currentUser;

export{
    setCurrentUserState,
    getCurrentUserState
}