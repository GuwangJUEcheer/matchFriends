import myaxios from '../plugins/my-axios';
import {setCurrentUserState,getCurrentUserState} from '../state/userState';
const getCurrentUser = async () =>{
    const user = getCurrentUserState();
    if(user){
        return user;
    }
    const res = await myaxios.get('/user/current');
    if(res.code===0){
        setCurrentUserState(res.data);
        return res.data;
    }else{
        return null;
    }
}

export default getCurrentUser;