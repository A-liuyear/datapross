import http from "@/http";
import { SysRole ,RoleListParam,RoleMenuParam} from "./RoleModel";

export const addRole=(param:SysRole)=>{
    return http.post('/api/role',param)
}
export const getRoleList=(param:RoleListParam)=>{
    return http.get('/api/role/list',param)
}

export const editRole=(param:SysRole)=>{
    return http.put('/api/role',param)
}

export const delRole=(roleId:string)=>{
    return http.delete(`/api/role/${roleId}`)
}

export const getSelectList=()=>{
    return http.get('/api/role/selectList')
}

export const saveRoleMenuApi=(param:RoleMenuParam)=>{
    return http.post('/api/role/saveRoleMenu',param)
}