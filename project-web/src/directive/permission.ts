import { useUserStore } from "@/store/user";
import { Directive } from "vue";
//自定义按钮权限指令
export const permission:Directive={
    mounted(el,binding){
        const userStore=useUserStore()

        //按钮上的权限字段
        const {value}=binding;
        //用户的所有权限字段
        const permissions=userStore.getCodeList;
        if(value&&value instanceof Array&& value.length>0){
            const permissionRoles=value;
            const hasPermission=permissions.some((role)=>{
                return permissionRoles.includes(role)
            })
            if(!hasPermission){
                el.style.display='none'
            }

        }else{
            throw new Error("按钮权限的传递方式 v-permission=[]")
        }
    }

}