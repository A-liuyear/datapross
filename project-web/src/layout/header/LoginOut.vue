<template>
    <el-dropdown :hide-on-click="false">
      <span class="el-dropdown-link">
        <img  class="userImg" src="@/assets/noBody.png"/>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="updateBtn">修改密码</el-dropdown-item>
          <el-dropdown-item @click="loginoutBtn">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <SysDialog
      :title="dialog.title"
      :width="dialog.width"
      :height="dialog.height"
      :visible="dialog.visible"
      @on-close="onClose"
      @on-confirm="commit"
    >
      <template v-slot:content>
        <el-form :model="upModel" ref="form" :rules="rules" label-width="80px" :inline="false" size="default">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input type="password" v-model="upModel.oldPassword"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="password">
            <el-input type="password" v-model="upModel.password"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirm">
            <el-input type="password" v-model="upModel.confirm"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </SysDialog>
  </template>
<script setup lang="ts">
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { ElMessage, FormInstance } from 'element-plus';
import { reactive ,ref} from 'vue';
import { updatePwdApi } from '@/api/user';
//import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import useInstance from '@/hooks/useInstance';
const {global} =useInstance()
const form=ref<FormInstance>()
//const router=useRouter()
const userStore=useUserStore()
//弹框
const {dialog,onClose,onShow}=useDialog()
const updateBtn=()=>{
  dialog.title="修改密码"
  dialog.height=180
  onShow()
  form.value?.resetFields()
}

const upModel=reactive({
  userId:'',
  oldPassword:'',
  password:'',
  confirm:''
})

const commit=()=>{
upModel.userId=userStore.getUserId
 form.value?.validate(async (valid)=>{
  console.log('1111'+valid)
   if(valid){
    console.log(upModel)
      //判断新密码与确认密码是否一致
      if(upModel.password!=upModel.confirm){
          ElMessage.warning('新密码和确认密码不一致')
          return;
      }
      let res=await updatePwdApi(upModel)
      if(res&&res.code==200){
           ElMessage.success(res.msg) 
           //清空缓存
           sessionStorage.clear()
           //跳转登录
           //router.push({path:'/login'})
           window.location.href='/login'
    }
   }
 })
}

const loginoutBtn=async ()=>{
  const confirm=await global.$confirm('确定退出登录吗?')
  if(confirm){
    sessionStorage.clear()
    //router.push({path:'/login'})
     window.location.href='/login'
  }
}

const rules=reactive({
  oldPassword:[
  {
            required:true,
            message:'请输入原密码',
            trigger:["change","blur"]
        }
      ],
   password:[
  {
            required:true,
            message:'请输入新密码',
            trigger:["change","blur"]
        }
      ], 
      confirm:[
  {
            required:true,
            message:'请输入确认密码',
            trigger:["change","blur"]
        }
      ]
})
</script>
<style scoped lang="scss">
.el-dropdown-link:focus{
    outline: none;
}
.userImg{
  height: 48px;
  width: 48px;
  border-radius: 50%;
  cursor: pointer;
  background-color: #FFF;

}
</style>