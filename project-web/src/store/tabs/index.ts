import { defineStore } from "pinia";
//定义选项卡数据
export type Tab={
    title:string;
    path:string;
}
//定义state 的数据类型
export type TabState={
    tabList:Tab[]
}
export const useTabStore=defineStore('useTabStore',{
 state:():TabState=>{
    return {
        tabList:[]
    }
 },
 getters:{
    getTabs(state){
        return state.tabList
    }
 },
 actions:{
    addTab(tab:Tab){
        if(this.tabList.some(item=>item.path===tab.path)){
            return;
        }
        if(tab.path==='/dashboard'){
            //如果是首页加到第一个
            this.tabList.unshift(tab)
        }else{
            this.tabList.push(tab)
        }
       
    }
 },
 persist:{
    enabled: true,
    strategies:[
        {storage:sessionStorage,paths:['tabList']}
    ]
 }


})