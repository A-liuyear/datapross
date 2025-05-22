import {createRouter,createWebHistory,RouteRecordRaw} from 'vue-router'
import Layout from '@/layout/Index.vue'
//动态生成
export const constantRoutes: Array<RouteRecordRaw>=[
    {
        path:'/login',
        name:'login',
        component:()=>import('@/views/login/login.vue')
    },

    {
       path: '/',
       component: Layout,
       redirect: '/dashboard',
       children:[
        {
            path: '/dashboard',
            component:()=>import('@/views/dashboard/Index.vue'),
            name: 'dashboard',

            meta:{
                title:'首页',
                icon:'House'
            }
        }
       ]
    }]
 
    //const routes:Array<RouteRecordRaw>=[
    // {
    //     path: '/system',
    //     component: Layout,
    //     name:'system',
    //     meta:{
    //         title:'系统管理',
    //         icon:"Setting",
    //         roles:["sys:manage"]
    //     },
    //     children:[
    //      {
    //          path: '/userList',
    //          component:()=>import('@/views/system/User/UserList.vue'),
    //          name: 'userList',
    //          meta:{
    //              title:'用户管理',
    //              icon:'UserFilled',
    //              roles:["sys:user"]
    //          }
    //      },
    //      {
    //         path: '/roleList',
    //         component:()=>import('@/views/system/Role/RoleList.vue'),
    //         name: 'roleList',
    //         meta:{
    //             title:'角色管理',
    //             icon:'Wallet',
    //             roles:["sys:role"]
    //         }
    //     },
    //     {
    //         path: '/menuList',
    //         component:()=>import('@/views/system/Menu/MenuList.vue'),
    //         name: 'menuList',
    //         meta:{
    //             title:'菜单管理',
    //             icon:'Menu',
    //             roles:["sys:menu"]
    //         }
    //     }
    //     ]
    //  }
   
   //]
const router=createRouter({
    history: createWebHistory(),
    routes:constantRoutes
})

export default router