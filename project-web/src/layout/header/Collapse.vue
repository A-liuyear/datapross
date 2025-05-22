<template>
    <el-icon @click="setCollapse" style="cursor: pointer;" color="#FFF" size="30">
        <component :is="status?Expand:Fold"></component>
    </el-icon>
</template>

<script setup lang="ts">
import {computed} from 'vue'
import {Fold,Expand} from "@element-plus/icons-vue"
import {useMenuStore} from '@/store/menu/index'
const menuStore=useMenuStore()
const status=computed(()=>{
    return menuStore.getCollapse
})
const setCollapse=()=>{
    menuStore.setCollapse(!menuStore.getCollapse)
}
</script>

<style scoped>

</style>