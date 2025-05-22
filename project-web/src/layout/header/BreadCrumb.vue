<template>
     <el-breadcrumb class="bread" separator="/">
        <el-breadcrumb-item v-for="item in tabs">{{item.meta.title}}</el-breadcrumb-item>
      </el-breadcrumb>
</template>

<script setup lang="ts">
import {ref,Ref,watch} from 'vue'
import {useRoute,RouteLocationMatched} from 'vue-router'
const route =useRoute()
//定义面包屑导航数据
const tabs:Ref<RouteLocationMatched[]>=ref([])
//获取面包屑数据
const getBreadcrumb=()=>{
    let mached=route.matched.filter((item)=>item.meta&&item.meta.title)
    console.log('mached:',mached)
    const firstBread=mached[0]
    if(firstBread.path!=='/dashboard'){
        mached=[{path:'/dashboard',meta:{title:'首页'}} as any].concat(mached)
    }
    tabs.value=mached
}
getBreadcrumb()
//监听当前路由
watch(()=>route.path,()=>{
    getBreadcrumb()
})
</script>

<style scoped>
.bread{
    margin-left: 20px;
}
:deep(.el-breadcrumb__inner){
    color: #fff !important;
}

:deep(.el-breadcrumb__inner a){
    color: #fff !important;
}

:deep(.el-breadcrumb__item){
    font-size: 16px !important;
}
</style>