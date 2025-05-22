import { defineStore } from "pinia";
import { getUserInfoApi } from "@/api/user";
export const useUserStore=defineStore('useUserStore',{
    state:()=>{
        return {
            userId:'',
            nickName:'',
            token:'',
            codeList:[]
        }
    },
    getters:{
        getUserId(state){
            return state.userId
        },
        getNickName(state){
            return state.nickName
        },
        getToken(state){
            return state.token
        },
        getCodeList(state){
            return state.codeList
        }
    },
    actions:{
        setUserId(userId:string){
            this.userId=userId
        },
        setNickName(nickName:string){
            this.nickName=nickName
        },
        setToken(token:string){
            this.token=token
        },
        getUserInfo(){
            return new Promise((resolve,reject)=>{
                getUserInfoApi(this.userId).then((res)=>{
                       if(res&&res.code==200){
                       this.codeList=res.data.permissions;
                       this.userId=res.data.userId
                    }
                    resolve(this.codeList)
            }).catch((error)=>{
                reject(error)
            })
        })
        }
            
    },
    persist:{
       enabled: true,
       strategies:[
           {storage:sessionStorage,paths:['userId','nickName','token']}
       ]
    }
})

