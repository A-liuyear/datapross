<template>
    <el-main>
      <el-button v-permission="['sys:menu:add']" type="primary" icon="Plus" size="default" @click="addBtn">新增</el-button>
      <el-table 
      style="margin-top: 20px;"
      default-expand-all 
      :data="tableList" 
      :height="tableHeight"
      border 
      stripe 
      row-key="menuId">
        <el-table-column label="菜单名称"  prop="title"/>
        <el-table-column label="菜单图标"  prop="icon">
           <template #default="scope">
            <el-icon v-if="scope.row.icon" >
              <component  :is="scope.row.icon"></component>
            </el-icon>
           </template>
          </el-table-column>
          <el-table-column label="类型"  prop="icon">
           <template #default="scope">
              <el-tag v-if="scope.row.type=='0'" size="default" type="danger">目录</el-tag>
              <el-tag v-if="scope.row.type=='1'" size="default" type="success">菜单</el-tag>
              <el-tag v-if="scope.row.type=='2'" size="default" type="primary">按钮</el-tag>
           </template>
          </el-table-column>
          <el-table-column label="上级菜单"  prop="parentName"/>
          <el-table-column label="路由名称"  prop="name"/>
          <el-table-column label="路由地址"  prop="path"/>
          <el-table-column label="组件路径"  prop="url"/>
          <el-table-column label="权限字段"  prop="code"/>
          <el-table-column label="序号"  prop="orderNum"/>
          <el-table-column label="操作" align="center"  width="220px" v-if="global.$hasPerm(['sys:menu:edit','sys:menu:delete'])">
            <template #default="scope">
            <el-button  v-if="global.$hasPerm(['sys:menu:edit'])"  type="primary" icon="Edit" size="default" @click="editBtn(scope.row)">编辑</el-button>
            <el-button  v-if="global.$hasPerm(['sys:menu:delete'])" type="danger" icon="Delete" size="default" @click="deleteBtn(scope.row.menuId)">删除</el-button>
          </template>
          </el-table-column>
      </el-table>
      
    

      <SysDialog
      :title="dialog.title"
      :width="dialog.width"
      :height="dialog.height"
      :visible="dialog.visible"
      @on-close="onClose"
      @on-confirm="commit"
      >
         <template v-slot:content>
            <el-form :model="addModel" ref="addForm" :rules="rules" label-width="80px" :inline="false" size="default">
              <el-form-item label="菜单类型" prop="type">
                <el-radio-group v-model="addModel.type">
                  <el-radio :value="'0'">目录</el-radio>
                  <el-radio :value="'1'">菜单</el-radio>
                  <el-radio :value="'2'">按钮</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-row :gutter="20">
                <el-col :span="12" :offset="0">
                   <el-form-item label="上级菜单" prop="parentId">
                      <el-tree-select 
                      v-model="addModel.parentId"
                      :data="treeList"
                      @check-change="treeClick"
                      :render-after-expand="false"
                      show-checkbox
                      :check-strictly="true"
                      >
                      </el-tree-select>
                   </el-form-item>
                </el-col>
                <el-col :span="12"  :offset="0">
                      <el-form-item label="菜单名称" prop="title">
                        <el-input v-model="addModel.title"></el-input>
                      </el-form-item>
                </el-col>
              </el-row>

              <el-row v-if="addModel.type!='2'"  :gutter="20">
                <el-col :span="12" :offset="0">
                      <el-form-item label="菜单图标" prop="icon">
                        <el-input v-model="addModel.icon"></el-input>
                      </el-form-item>
                </el-col>
                <el-col :span="12"  :offset="0">
                      <el-form-item label="路由名称" prop="name">
                        <el-input v-model="addModel.name"></el-input>
                      </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="20">
                <el-col :span="12" :offset="0">
                      <el-form-item label="菜单序号" prop="orderNum">
                        <el-input v-model="addModel.orderNum"></el-input>
                      </el-form-item>
                </el-col>
                <el-col :span="12"  :offset="0">
                      <el-form-item label="权限字段" prop="code">
                        <el-input v-model="addModel.code"></el-input>
                      </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col v-if="addModel.type!='2'" :span="12" :offset="0">
                      <el-form-item label="路由地址" prop="path">
                        <el-input v-model="addModel.path"></el-input>
                   </el-form-item>
                </el-col>
                <el-col v-if="addModel.type=='1'" :span="12"  :offset="0">
                      <el-form-item label="组件路径" prop="url">
                        <el-input v-model="addModel.url"></el-input>
                   </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col v-if="addModel.type=='1'" :span="18"  :offset="0">
                      <el-form-item label="是否显示" prop="vshow">
                        <el-radio-group v-model="addModel.vshow">
                        <el-radio :value="2" size="large">显示</el-radio>
                        <el-radio :value="1" size="large">不显示</el-radio>
                      </el-radio-group>
                   </el-form-item>
                </el-col>
              </el-row>
            </el-form>
            
         </template>
      </SysDialog>
    </el-main>
</template>

<script setup lang="ts">
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { ElMessage, FormInstance } from 'element-plus';
import { onMounted, reactive, ref,nextTick } from 'vue';
import { addApi,listApi,getParentApi,editApi,deleteApi } from '@/api/menu';
import { MenuType } from '@/api/menu/MenuModel';
import useInstance from '@/hooks/useInstance';
//获取全局global
const {global}=useInstance()
const addForm=ref<FormInstance>()
const {dialog,onClose,onShow} =useDialog();
const addBtn=()=>{
  dialog.title="新增"
  dialog.height=310
  getParent()
  onShow()
  addModel.menuId=''
  //清空表单
  addForm.value?.resetFields()
}

const treeList=ref([])

const getParent=async()=>{
  let res=await getParentApi()
  if(res&&res.code==200){
    treeList.value=res.data
  }
}

const addModel=reactive({
  menuId:'',
  parentId:'',
  title:'',
  code:'',
  name:'',
  path:'',
  url:'',
  type:'',
  icon:'',
  parentName:'',
  orderNum:'',
  vshow:'2'
})


const commit=()=>{
  addForm.value?.validate(async(valid)=>{
     if(valid){
      let res=null;
      if(addModel.menuId){
        res=await editApi(addModel)
      }else{
        res=await addApi(addModel)
      }
       if(res&&res.code==200){
            ElMessage.success(res.msg)
            onClose()
       }
       getList()
     }
  })
}

const treeClick=(item:any)=>{
  addModel.parentName=item.title
}


const tableList=ref([])
const getList=async()=>{
  let res=await listApi()
  if(res&&res.code==200){
    tableList.value=res.data
  }
}

const editBtn=async(row:MenuType)=>{
  dialog.title="编辑"
  dialog.height=310
  await getParent()
  onShow()
  nextTick(()=>{
    Object.assign(addModel,row)
  });
 addForm.value?.resetFields()
}

const deleteBtn=async (menuId:string)=>{
  const confirm =await global.$confirm("确定删除该条数据?")
  if(confirm){
    let res =await deleteApi(menuId)
    if(res&&res.code==200){
      ElMessage.success(res.msg)
      getList()
    }
}
}

//表格高度
const tableHeight=ref(0)

onMounted(()=>{
  getList()
  nextTick(()=>{
    tableHeight.value=window.innerHeight-220
  })
})
const rules=reactive({
  parentId:[
  {
            required:true,
            message:'请选择上级菜单',
            trigger:["change","blur"]
        }
  ],
  title:[
  {
            required:true,
            message:'请填写菜单名称',
            trigger:["change","blur"]
        }
  ],
  path:[
  {
            required:true,
            message:'请填写路由地址',
            trigger:["change","blur"]
        }
  ],
  url:[
  {
            required:true,
            message:'请填写组件路径',
            trigger:["change","blur"]
        }
  ],
  name:[
  {
            required:true,
            message:'请填写路由名称',
            trigger:["change","blur"]
        }
  ],
  code:[
  {
            required:true,
            message:'请填写权限字段',
            trigger:["change","blur"]
        }
  ],
  type:[
  {
            required:true,
            message:'请填写菜单类型',
            trigger:["change","blur"]
        }
  ],
  icon:[
  {
            required:true,
            message:'请填写图标名称',
            trigger:["change","blur"]
        }
  ],
  orderNum:[
  {
            required:true,
            message:'请填写菜单序号',
            trigger:["change","blur"]
        }
  ]
  
})

</script>

<style scoped>
 .el-row{
  width: 100%;
 }
</style>