<template>
    <SysDialog
    :title="dialog.title"
    :width="dialog.width"
    :height="dialog.height"
    :visible="dialog.visible"
    @on-close="onClose"
    @on-confirm="commit"
    >
        <template v-slot:content>
            <el-tree ref="menuTreeRef" node-key="menuId" default-expand-all :default-checked-keys="menuTreeData.checkList" show-checkbox :data="menuTreeData.menuList" :props="defaultProps"/>
        </template>
    </SysDialog>
</template>

<script setup lang="ts">
import { getMenuTreeApi } from '@/api/user';
import { saveRoleMenuApi } from '@/api/role';
import { reactive ,ref} from 'vue';
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { useUserStore } from '@/store/user';
import { ElMessage, ElTree } from 'element-plus';
const {dialog,onClose,onShow}=useDialog() 
const userStore=useUserStore()
const menuTreeRef=ref<InstanceType<typeof ElTree>>()
//树的属性配置
const defaultProps={
    children:'children',
    label:'title'
}
const menuTreeData=reactive({
    menuList:[],
    checkList:[]

})
const params=reactive({
    userId:'',
    roleId:''
})

const getMenuTree =async()=>{
    let res= await getMenuTreeApi(params)
    if(res&&res.code==200){
        menuTreeData.menuList=res.data.menuList;
        menuTreeData.checkList=res.data.checkList;
        //数据回显 判断角色原来是否已经分配过权限 如果有  回显
        if(menuTreeData.checkList.length>0){
            let newArr:any=[]
            menuTreeData.checkList.forEach((menuId)=>{
                checked(menuId,menuTreeData.menuList,newArr)
            }
         )
         menuTreeData.checkList=newArr
    }     
}
}
const commitParam=reactive({
    roleId:'',
    list:[] as string[]
})
const show=async (roleId:string,name:string)=>{
    commitParam.roleId=roleId
    commitParam.list=[]
    menuTreeData.checkList=[]
    menuTreeData.menuList=[]
    params.roleId=roleId
    params.userId=userStore.getUserId
    dialog.height=450;
    dialog.width=300;
    dialog.title='为【'+name+'】分配菜单'
    await getMenuTree()
    onShow()
}

const checked=(id:number,data:any,newArr:any)=>{
    data.forEach((item:any) => {
        if(item.menuId==id){
            if(item.children&&item.children.length==0){
                newArr.push(item.menuId)
        }
    }  else{
        if(item.children&&item.children.length!=0){
            //递归调用
            checked(id,item.children,newArr)
    } 
}
    });
}


const commit=async()=>{
  //获取选择的菜单
  const checkIds=menuTreeRef.value?.getCheckedKeys() as string[]
  const halfCheckIds=menuTreeRef.value?.getHalfCheckedKeys() as string[]
  let ids=checkIds
  if(halfCheckIds&&halfCheckIds.length>0){
    ids=checkIds?.concat(halfCheckIds)
  }
  commitParam.list=ids;
  if(ids.length==0){
    return ElMessage.warning("请选择菜单")
  }
    
  let res=await saveRoleMenuApi(commitParam)
  if(res&&res.code==200){
    ElMessage.success(res.msg)
    onClose()
  } 
}

//暴漏出去 给外部组件使用
defineExpose({
    show
})
</script>

<style scoped>

</style>