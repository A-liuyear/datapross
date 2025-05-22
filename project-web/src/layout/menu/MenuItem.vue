<template>
   <template v-for="menu in menuList" :key="menu.path">
    <!--有下级菜单-->
        <el-sub-menu v-if="menu.children && menu.children.length>0" :index="menu.path">
            <template #title>
                <el-icon>
                    <component :is="menu.meta.icon"></component>
                </el-icon>
                <span>{{menu.meta.title}}</span>
            </template>
            <!--生成下级递归调用-->
            <MenuItem :menuList="menu.children"></MenuItem>
        </el-sub-menu>

         <!--没有下级菜单-->
        <el-menu-item style="color: #f4f4f5;" v-else :index="menu.path" v-if="menu.vshow!=1">
            <el-icon>
                    <component :is="menu.meta.icon"></component>
             </el-icon>
            <template #title>{{menu.meta.title}}</template>
        </el-menu-item>
   </template>
    
</template>

<script setup lang="ts" >
defineProps(['menuList'])
</script>

<style scoped >

</style>