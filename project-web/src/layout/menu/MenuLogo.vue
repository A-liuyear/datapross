<template>
    <div class="logo">
         <img :src="MenuLogo" >
         <span v-if="show" class="logo-title">{{title}}</span>
    </div>
</template>

<script setup lang="ts">
import {ref,watch} from 'vue'
import MenuLogo from '@/assets/logo.png'
import {useMenuStore} from '@/store/menu/index'
const menuStore=useMenuStore()
const title =ref('智能数据分析平台')
//解决伸缩僵硬的问题
const show=ref(true)
watch(()=>menuStore.getCollapse,(collapsed:boolean)=>{
    setTimeout(() => {
        show.value=!collapsed
       }, 200);
})
</script>

<style scoped>
.logo{
    display: flex;
    width: 100%;
    height: 60px;
    background-color: #2b2f3a;
    text-align: center;
    cursor: pointer;
    align-items: center;

    img{
        width: 40px;
        height: 40px;
        margin-right: 10px;
        margin-left: 10px;
    }
    .logo-title{
        color:#FFF;
        font-weight: 800;
        line-height: 60px;
        font-size: 21px;
        font-family: FangSong;
    }
}
</style>