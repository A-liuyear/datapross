<template>
    <MenuLogo></MenuLogo>
     <el-menu
    :default-active="defaultActive"
    class="el-menu-vertical-demo dark-mode"
    :collapse="isCollapse"
    @open="handleOpen"
    @close="handleClose"
    background-color="#304156"
    unique-opened
    router
  >
    <MenuItem :menuList='menuList'></MenuItem>
  </el-menu>
</template>

<script setup lang="ts">
import { computed,ref} from 'vue'
import {useMenuStore} from '@/store/menu/index'
import { useRoute } from 'vue-router';
import MenuItem from '@/layout/menu/MenuItem.vue'
import MenuLogo  from '@/layout/menu/MenuLogo.vue'
const menuList=ref([]) as any
const menuStore=useMenuStore()
const isCollapse=computed(()=>{
    return menuStore.getCollapse
})
const route=useRoute()
//当前激活的菜单
const defaultActive = computed(()=>{
    const {path} =route
    //console.log("path:",path)
    return path
})
menuList.value=menuStore.getMenus


const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>

<style scoped >
.el-menu-vertical-demo:not(.el-menu--collapse){
    width: 230px;
    min-height: 400px;
}
.el-menu{
    border-right: none;
}
:deep(.el-sub-menu .el-sub-menu__title){
    color:#f4f4f5 !important;
}
:deep(.el-menu .el-menu-item){
    color:#bfcbd9
}

:deep(.el-menu-item.is-active){
    color:#409eff !important;
}

:deep(.is-opened .el-menu-item){
    background-color: #001528 !important;
}
/*鼠标移动菜单的颜色*/
:deep(.el-menu-item:hover){
    background-color: #001528 !important;
}
</style>