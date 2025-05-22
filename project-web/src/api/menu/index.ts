import http from '@/http'
import { MenuType } from './MenuModel'
export const getParentApi=()=>{
    return http.get("/api/menu/parentMenus")
}

export const addApi=(param:MenuType)=>{
    return http.post("/api/menu",param)
}

export const listApi=()=>{
    return http.get("/api/menu/list")
}

export const editApi=(param:MenuType)=>{
    return http.put("/api/menu",param)
}

export const deleteApi=(menuId:string)=>{
    return http.delete(`/api/menu/${menuId}`)
}

export const getMenuListApi=(userId:string)=>{
    return http.get("/api/menu/getMenuList",{userId:userId})
}