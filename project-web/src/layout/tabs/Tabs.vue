<template>
    <el-tabs
    v-model="activeTab"
    type="card"
    closable
    class="demo-tabs"
    @tab-remove="removeTab"
    @tab-click="clickBtn"
  >
    <el-tab-pane
      v-for="item in tabsList"
      :key="item.path"
      :label="item.title"
      :name="item.path"
    >
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import {computed, ref,watch,onMounted} from 'vue'
import{Tab, useTabStore } from '@/store/tabs/index'
import { useRoute ,useRouter} from 'vue-router'
import { TabsPaneContext } from 'element-plus';
const tabStore=useTabStore()
const route=useRoute()
const router=useRouter()
//选中选项卡的数据
const activeTab=ref('')
const tabsList=computed(()=>{
    return tabStore.getTabs
})
const clickBtn=(pane: TabsPaneContext)=>{
    const {props}=pane
    //跳转路由
    router.push({path:props.name as string})
}
const removeTab = (targetName: string) => {
  const tabs = tabsList.value
  let activeName = activeTab.value
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

  //重新设置激活的选项卡
  activeTab.value = activeName
  //重新设置选项卡数据
  tabStore.tabList = tabs.filter((tab) => tab.path !== targetName)
  //跳转路由
  router.push({path:activeName})
}
const addTab=()=>{
    const {path,meta} =route
    const tab:Tab={
        path:path,
        title:meta.title as string
    }
    tabStore.addTab(tab)
}
//添加选项卡 监听当前路由
watch(()=>route.path,
()=>{
    setActiveTab()
    addTab()
})
//设置激活的选项卡
const setActiveTab=()=>{
    activeTab.value=route.path
}
onMounted(()=>{
    setActiveTab()
    addTab()
})
</script>

<style scoped>
:deep(.el-tabs__header){
    margin:0px;
}
:deep(.el-tabs__item){
    height: 26px !important;
    line-height: 26px !important;
    text-align: center !important;
    border: 1px solid #d8dce5 !important;
    margin: 0px 3px !important;
    color: #495060;
    font-size: 12px !important;
    padding: 0px 10px !important;
}
:deep(.el-tabs__nav){
    border:none !important;
}
:deep(.is-active){
    border-bottom: 1px solid transparent !important;
    border: 1px solid #42b983 !important;
    background-color: #42b983 !important;
    color: #fff !important;
}
:deep(.is-tabs__item:hover){
    color: #495060 !important;
}
:deep(.is-active:hover){
    color: #fff !important;
}

:deep(.el-tabs__nav-next){
    line-height: 26px !important;
}
:deep(.el-tabs__nav-prev){
    line-height: 26px !important;
}
:deep(.el-tabs__header){
    border-bottom: none !important;
}
</style>