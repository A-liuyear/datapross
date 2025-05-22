import http from "@/http";
import { User, UserListParam,loginParam,MenuParam,UpdatePwdParam,registerParam} from "./userModel";
export const addUser=(param:User)=>{
    return http.post('/api/user',param)
}

export const registerApi=(param:registerParam)=>{
    return http.post('/api/user/register',param)
}



export const getList=(param:UserListParam)=>{
 return http.get("/api/user/list",param)
}

export const editUser=(param:User)=>{
    return http.put('/api/user',param)
}

export const delUser=(userId:string)=>{
    return http.delete(`/api/user/${userId}`)
}
//根据用户id查询角色
export const getRoleList=(userId:string)=>{
    return http.get("/api/user/getRolelist",{userId:userId})
}

export const resetPwd=(param:{userId:string})=>{
    return http.post('/api/user/resetPassword',param)
}

export const getImgApi=()=>{
    return http.post('/api/user/getImage')
}

export const loginApi=(param:loginParam)=>{
    return http.post('/api/user/login',param)
}

export const getMenuTreeApi=(param:MenuParam)=>{
    return http.get('/api/user/getMenusTree',param)
    
}

export const updatePwdApi=(param:UpdatePwdParam)=>{
    return http.post('/api/user/updatePassword',param)
}

export const getUserInfoApi=(userId:string)=>{
    return http.get('/api/user/getUserInfo',{userId:userId})
}

