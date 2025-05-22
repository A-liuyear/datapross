import { getMenuListApi } from "@/api/menu";
import { defineStore } from "pinia";
import { RouteRecordRaw } from "vue-router";
import Layout from '@/layout/Index.vue';
import Main from "@/layout/center/Main.vue";

const modules=import.meta.glob('../../views/**/*.vue')
export const useMenuStore=defineStore('menuStore',{
    state:()=>{
        return {
            collapse:false,
            menuList:[
                {
                    path: '/dashboard',
                    component:'Layout',
                    name: 'dashboard',
                    meta:{
                        title:'首页',
                        icon:'HomeFilled',
                        roles:['sys:dashboard']   
                    }
                }
            ]
        }
    },
    getters:{
        getCollapse(state){
            return state.collapse
        },
        getMenus(state){
            return state.menuList
        }
    },
    actions:{
        setCollapse(collapse:boolean){
            this.collapse=collapse
        },
        getMenuList(router:any,userId:string){
            return new Promise((resolve,reject)=>{
                getMenuListApi(userId).then((res)=>{
                    let accessRoute;
                    if(res&&res.code==200){
                      //生成路由
                      accessRoute=generateRoute(res.data,router) as any
                      this.menuList=this.menuList.concat(accessRoute)
                    } 
                    resolve(this.menuList)
                }).catch((error)=>{
                    reject(error)   
                }
                )
            })
        }
    }
})
//动态生成路由
export function generateRoute(routes:RouteRecordRaw[],router:any){
    const res:Array<RouteRecordRaw>=[];
    routes.forEach((route:any)=>{
        const temp={...route}
        const component=temp.component;
        if(route.component){
            if(component=='Layout'){
                temp.component=Layout;
            }else{
                temp.component=modules[`../../${component}`]
            }
        }
        //有下级
        if(temp.children&&temp.children.length>0){
            if(route.component!='Layout'){
                temp.component=Main;
            }
            //递归：生成下级
            temp.children=generateRoute(temp.children,router)
        }
        res.push(temp)
        //加入路由
        router.addRoute(temp)
    })
   return res;
}

