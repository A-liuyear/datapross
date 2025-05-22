<template>
    <el-main>
        <el-form :model="searchParam"   :inline="true" size="default">
            <el-form-item label="">
                <el-input placeholder="请输入关键字" v-model="searchParam.roleName"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button  icon="Search"  @click="searchBtn">搜索</el-button>
                <el-button  icon="Close" plain type="danger" @click="resetBtn">重置</el-button>
                <el-button v-if="global.$hasPerm(['sys:role:add'])" icon="Plus" type="primary" @click="addBtn">新增</el-button>
            </el-form-item>
        </el-form>
        <!--表格-->
        <el-table :height="tableHeight" :data="tableList" border stripe>
            <el-table-column prop="roleName" label="角色名称"></el-table-column>
            <el-table-column prop="remark" label="角色备注"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="updateTime" label="更新时间"></el-table-column>
            <el-table-column  label="操作" width="320" align="center" v-if="global.$hasPerm(['sys:role:edit','sys:role:assignMenu','sys:role:delete'])">
                <template #default="scope">
                    <el-button  v-if="global.$hasPerm(['sys:role:edit'])" type="primary" icon="Edit" size="default" @click="editBtn(scope.row)">
                     编辑
                    </el-button>
                    <el-button   v-if="global.$hasPerm(['sys:role:assignMenu'])"type="success" icon="Edit" size="default" @click="assignMenuBtn(scope.row)">
                     分配菜单
                    </el-button>
                    <el-button v-if="global.$hasPerm(['sys:role:delete'])"  type="danger" icon="Delete" size="default" @click="delBtn(scope.row.roleId)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--分页-->
        <el-pagination
            @size-change="sizeChange"
            @current-change="currentChange"
            :current-page.sync="searchParam.currentPage"
            :page-sizes="[10,20, 40, 80, 100]"
            :page-size="searchParam.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="searchParam.total" background>
        </el-pagination>
        

        <!--新增 编辑弹框-->
        <SysDialog
         :title="dialog.title"
         :width="dialog.width"
         :height="dialog.height"
         :visible="dialog.visible"
         @on-close="onClose"
         @on-confirm="onCommit"
        >
            <template v-slot:content>
                <el-form :model="addModel"  ref="addRef" :rules="rules" label-width="80px" :inline="false" size="default">
                    <el-form-item  prop="roleName" label="角色名称">
                        <el-input v-model="addModel.roleName"></el-input>
                    </el-form-item>
                    <el-form-item prop="remark" label="备注">
                        <el-input v-model="addModel.remark"></el-input>
                    </el-form-item>
                </el-form>
            </template>
        </SysDialog>
        <!-- 分配菜单 -->
        <AssignTree ref="assignTree"></AssignTree>
    </el-main>
</template>

<script setup lang="ts">
import { reactive ,ref,nextTick} from 'vue';
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { ElMessage, FormInstance } from 'element-plus';
import {addRole,getRoleList,editRole,delRole} from '@/api/role/index'
import { onMounted } from 'vue';
import {SysRole} from '@/api/role/RoleModel'
import useInstance from '@/hooks/useInstance'
import AssignTree from './AssignTree.vue';

//获取全局global
const {global}=useInstance()
//表单ref属性
const addRef=ref<FormInstance>()
const {dialog,onClose,onShow} =useDialog()

//菜单树的ref属性
const assignTree=ref()
const searchParam=reactive({
    total:0,
    currentPage:1,
    pageSize:10,
    roleName:''
})
//新增表单
const addModel=reactive({
    roleId:'',
    roleName:'',
    remark:''
})
//表单验证规则
const rules=reactive({
    roleName:[
        {
            required:true,
            message:'请填写角色名称',
            trigger:"change"
        }
    ],
    remark:[
        {
            required:true,
            message:'请填写备注',
            trigger:"change"
        }
    ]
})
const addBtn=()=>{
    dialog.title="新增"
    dialog.height=180
    onShow()
    addModel.roleId=''
    //清空表单
    addRef.value?.resetFields()
}
//编辑按钮
const editBtn=(row:SysRole)=>{
    dialog.title="编辑"
    dialog.height=180
    nextTick(()=>{
        Object.assign(addModel,row)
    })
    onShow()
}

const assignMenuBtn=(row:SysRole)=>{
    assignTree.value.show(row.roleId,row.roleName)
}
//删除按钮
const delBtn=async(roleId:string)=>{
    const confirm=await global.$confirmFunc("确定删除该数据吗?");//返回boolean
    if(confirm){
        let res=await delRole(roleId)
        if(res&&res.code==200){
           ElMessage.success(res.msg) 
           roleList()
    }
}
}
const sizeChange=(size:number)=>{
    searchParam.pageSize=size;
    roleList()
}

const currentChange=(page:number)=>{
    searchParam.currentPage=page;
    roleList()
}


//表单提交
const onCommit=()=>{
    addRef.value?.validate(async (valid)=>{
        if(valid){
            console.log("表单验证通过")
            //提交请求
            let res=null
            if(addModel.roleId){
                res=await editRole(addModel)
            }else{
                res=await addRole(addModel)
            }
            if(res&&res.code==200){
                ElMessage.success(res.msg)
                roleList()
                onClose()
        }
    }
})
}

    
const resetBtn=()=>{
    searchParam.roleName=''
    searchParam.currentPage=1
    roleList()
}

const searchBtn=()=>{
    roleList()
}
//表格高度
const tableHeight=ref(0)
//表格数据
const tableList=ref([])

const roleList=async ()=>{
    let res=await getRoleList(searchParam);
    if(res&&res.code==200){
        //设置表格数据
        tableList.value=res.data.records
        //设置分页总条数
        searchParam.total=res.data.total
    }
        
}
onMounted(()=>{
    nextTick(()=>{
        tableHeight.value=window.innerHeight-230
    })
    roleList()
})
</script>

<style scoped>

</style>