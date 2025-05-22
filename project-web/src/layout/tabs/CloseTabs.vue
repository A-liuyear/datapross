<template>
    <div class="close">
        <el-dropdown>
        <span class="el-dropdown-link">
        <el-icon class="el-icon--right">
        <close/>
      </el-icon>
      关闭
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="closeCurrent">关闭当前</el-dropdown-item>
        <el-dropdown-item @click="closeLeft">关闭左侧</el-dropdown-item>
        <el-dropdown-item @click="closeRight">关闭右侧</el-dropdown-item>
        <el-dropdown-item @click="closeAll">关闭所有</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
    </div>
</template>

<script setup lang="ts">
import {useRoute,useRouter} from 'vue-router'
import { Tab, useTabStore } from '@/store/tabs';
import {Close} from "@element-plus/icons-vue";
const route=useRoute();
const tabStore=useTabStore();
const router=useRouter();
//关闭当前
const closeCurrent=()=>{
    const targetName=route.path;
    if(targetName==='/dashboard'){
      return ;
    }
    const tabs = tabStore.getTabs
    let activeName = route.path
    if (activeName === targetName) {
        tabs.forEach((tab:Tab, index:number) => {
        if (tab.path === targetName) {
            const nextTab = tabs[index + 1] || tabs[index - 1]
            if (nextTab) {
            activeName = nextTab.path
            }
        }
        })
    }
    //重新设置选项卡数据
    tabStore.tabList = tabs.filter((tab) => tab.path !== targetName)
    //跳转路由
    router.push({path:activeName})
}
//关闭右侧
const closeRight=()=>{
  const path=route.path;
  //找到当前路由的索引
  let index=tabStore.tabList.findIndex(item=>item.path===path);
  tabStore.tabList.splice(index+1)//删除当前索引之后的所有数据

}
//关闭左侧
const closeLeft=()=>{
  const path=route.path;
  //找到当前路由的索引
  let index=tabStore.tabList.findIndex(item=>item.path===path);
  tabStore.tabList.splice(1,index-1)//删除当前索引之后的所有数据

}

//关闭所有
const closeAll=()=>{
  tabStore.tabList.splice(1,tabStore.tabList.length)//删除当前索引之后的所有数据
  //跳转首页
  router.push({path:'/dashboard'})
}
</script>

<style scoped>
.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}
.el-dropdown-link:focus{
    outline: none;
}
.close{
    width: 60px;
    height: 40px;
    position: fixed;
    top:100px;
    right: 0;
    z-index: 999;
    display: flex;
    align-items: center;
    justify-content: center;
    border-left: 1px solid #e4e7ed;
}
</style>