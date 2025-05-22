import { useUserStore } from "@/store/user";
export default function hasPerm(value:any){
    const userStore=useUserStore()
    const permissions=userStore.getCodeList;

    if(value&&value instanceof Array&& value.length>0){
        const permissionRoles=value;
        const hasPermission=permissions.some((role)=>{
            return permissionRoles.includes(role)
        })
       return hasPermission;

    }else{
        throw new Error('v-if="global.$hasPerm("[\'add\',\'edit\']")')
    }
}