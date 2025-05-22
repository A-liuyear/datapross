import router from "./router";
import { useUserStore } from "./store/user";
import { useMenuStore } from "./store/menu";
//定义白名单
const  whiteList=['/login']
router.beforeEach(async(to,from,next)=>{
     console.log(from)
    const userStore=useUserStore()
    const menuStore=useMenuStore()
    //获取token
    const token=userStore.getToken
    if(token){
        if(to.path==='/login' || to.path==='/'){
            next({path:'/'})
        }else{
            const hasRoles=userStore.getCodeList
            if(hasRoles&&hasRoles.length>0){
                next()
            }else{
                try{
                    //如果没有 则从服务器获取生成动态路由
                    await userStore.getUserInfo()
                    //获取菜单数据
                    await menuStore.getMenuList(router,userStore.getUserId)
                    //等待路由完全挂载
                    next({...to,replace:true})
                }catch(error){
                    sessionStorage.clear()
                    next({path:'/login'})
                }
               
            }
        }
    
    }else{
        if(whiteList.indexOf(to.path)!==-1){
            next()
         }else{
            next({path:'/login'})
         }
}
})