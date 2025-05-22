<template>
    <el-main height="">
        <el-form :model="searchParam"  :inline="true" size="default">
            <el-form-item >
                <el-input placeholder="请输入姓名" v-model="searchParam.nickName"></el-input>
            </el-form-item>
            <el-form-item >
                <el-input placeholder="请输入电话" v-model="searchParam.phone"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button  icon="Search"  @click="searchBtn">搜索</el-button>
                <el-button  icon="Close" plain type="danger" @click="resetBtn">重置</el-button>
                <el-button v-if="global.$hasPerm(['sys:user:add'])"  icon="Plus" type="primary" @click="addBtn">新增</el-button>
            </el-form-item>
        </el-form>

         <!--表格-->
         <el-table :height="tableHeight" :data="tableList" border stripe >
            <el-table-column type="index" width="50" label="序号" />
            <el-table-column prop="nickName" label="姓名"></el-table-column>
            <el-table-column prop="sex" label="性别">
                <template #default="scope">
                        <el-tag v-if="scope.row.sex=='0'"  type="primary" size="default" effect="dark" >男</el-tag>
                        <el-tag v-if="scope.row.sex=='1'"  type="danger" size="default" effect="dark" >女</el-tag>
                </template>
            </el-table-column>

            <el-table-column prop="phone" label="电话"></el-table-column>
            <el-table-column prop="email" label="邮箱"></el-table-column>
            <el-table-column prop="pointNum" label="积分"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="updateTime" label="更新时间"></el-table-column>
            <el-table-column  label="操作" width="320" align="center" v-if="global.$hasPerm(['sys:user:edit','sys:user:resetPwd','sys:user:delete'])">
                <template #default="scope">
                    <el-button v-if="global.$hasPerm(['sys:user:edit'])"  type="primary" icon="Edit" size="default" @click="editBtn(scope.row)">
                     编辑
                    </el-button>
                    <el-button  v-if="global.$hasPerm(['sys:user:resetPwd'])"   type="warning" icon="Setting" size="default" @click="resetPwdBtn(scope.row.userId)">
                     重置密码
                    </el-button>
                    <el-button  v-if="global.$hasPerm(['sys:user:delete'])" type="danger" icon="Delete" size="default" @click="delBtn(scope.row.userId)">删除</el-button>
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
        <SysDialog
        :title="dialog.title"
        :width="dialog.width"
        :visible="dialog.visible"
        :height="dialog.height"
        @on-close="onClose"
        @on-confirm="onCommit"
        >
        <template v-slot:content>
            <el-form
                :model="addModel"
                ref="addForm"
                :rules="rules"
                label-width="80px"
                :inline="false"
                size="default">
            <el-row >
                <el-col :span="12" :offset="0">
                        <el-form-item  prop="nickName" label="姓名">
                            <el-input v-model="addModel.nickName"></el-input>
                        </el-form-item>
                </el-col>
                <el-col :span="12" :offset="0">
                        <el-form-item  prop="sex" label="姓别">
                            <el-radio-group v-model="addModel.sex">
                                <el-radio :label="'0'">男</el-radio>
                                <el-radio :label="'1'">女</el-radio>
                            </el-radio-group>
                        </el-form-item>
                </el-col>
            </el-row>

            <el-row >
                <el-col :span="12" :offset="0">
                        <el-form-item prop="phone"  label="电话">
                            <el-input v-model="addModel.phone"></el-input>
                        </el-form-item>
                </el-col>
                <el-col :span="12" :offset="0">
                        <el-form-item prop="email" label="邮箱">
                            <el-input v-model="addModel.email"></el-input>
                        </el-form-item>
                </el-col>
            </el-row>

            <el-row >
                <el-col :span="12" :offset="0">
                        <el-form-item prop="roleId" label="角色">
                            <SelectChecked ref="selectRef" :options="options" @selected="selected" :roleIds="roleIds">
                            </SelectChecked>
                        </el-form-item>
                </el-col>
                <el-col :span="12" :offset="0">
                        <el-form-item prop="userName" label="账号">
                            <el-input v-model="addModel.userName"></el-input>
                        </el-form-item>
                </el-col>
            </el-row>

            <el-row  v-if="tags=='0'">
                <el-col :span="12" :offset="0">
                        <el-form-item  prop="password" label="密码">
                            <el-input type="password" v-model="addModel.password"></el-input>
                        </el-form-item>
                </el-col>
                <el-col :span="12" :offset="0">
                        <el-form-item  prop="pointNum" label="积分">
                            <el-input-number v-model="addModel.pointNum"  :min="0" />
                        </el-form-item>
                </el-col>
            </el-row>
            <el-row  v-else>
                <el-col :span="12" :offset="0">
                        <el-form-item  prop="pointNum" label="积分">
                            <el-input-number v-model="addModel.pointNum"  :min="0"  />
                        </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        </template>
        </SysDialog>
    </el-main>
    
</template>

<script setup lang="ts">
import { onMounted, reactive,ref ,nextTick} from 'vue';
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { ElMessage, FormInstance } from 'element-plus';
import SelectChecked from '@/components/SelectChecked.vue';
import { getSelectList } from '@/api/role';
import {addUser,getList,editUser,delUser,getRoleList,resetPwd} from '@/api/user/index'
import {User} from '@/api/user/userModel'
import useInstance from '@/hooks/useInstance'
//获取全局global
const {global}=useInstance()
const {dialog,onClose,onShow}=useDialog()
const selectRef=ref()
const addForm=ref<FormInstance>()
   
const searchParam=reactive({
    phone:'',
    nickName:'',
    currentPage:1,
    pageSize:10,
    total:0
})
const addModel=reactive({
    userId:'',
    userName:'',
    password:'',
    phone:'',
    email:'',
    sex:'',
    nickName:'',
    roleId:'',
    pointNum:0
    
})

//表格高度
const tableHeight=ref(0)

const tags=ref('')

let options=ref([])

const rules=reactive({
    nickName:[
        {
            required:true,
            message:'请填写姓名',
            trigger:["change","blur"]
        }
    ],
    sex:[
        {
            required:true,
            message:'请填写性别',
            trigger:["change","blur"]
        }
    ],
    phone:[
        {
            required:true,
            message:'请填写电话',
            trigger:["change","blur"]
        }
    ],
    email:[
        {
            required:true,
            message:'请填写邮箱',
            trigger:["change","blur"]
        }
    ],
    userName:[
        {
            required:true,
            message:'请填写账号',
            trigger:["change","blur"]
        }
    ],
    password:[
        {
            required:true,
            message:'请填写密码',
            trigger:["change","blur"]
        }
    ],
    roleId:[
        {
            required:true,
            message:'请填写角色',
            trigger:["change","blur"]
        }
    ]
})

const addBtn=()=>{
    dialog.title='新增';
    dialog.height=230
    tags.value='0'
    onShow()
    options.value=[]
    roleIds.value=[]
    getSelect()
   
    addModel.userId='';
    nextTick(()=>{
        selectRef.value.clear()
    })
    //清空表单
    addForm.value?.resetFields()
}


const resetPwdBtn=async(userId:string)=>{
    const confirm=await global.$confirmFunc("确定重置密码吗?");//返回boolean
    if(confirm){
        let res=await resetPwd({userId:userId})
        if(res&&res.code==200){
           ElMessage.success(res.msg) 
           list()
    }
}
}

//编辑按钮
const editBtn=async (row:User)=>{
    dialog.title="编辑"
    dialog.height=230
    options.value=[]
    roleIds.value=[]
    await getSelect()
    //查询角色
    await roleList(row.userId)
    onShow()
    nextTick(()=>{
        Object.assign(addModel,row)
        //设置角色id
        addModel.roleId=roleIdJoin.value
        addModel.password=''
    })
    
     //清空表单
     addForm.value?.resetFields()
   
}

const sizeChange=(size:number)=>{
    searchParam.pageSize=size;
    list()
}

const currentChange=(page:number)=>{
    searchParam.currentPage=page;
    list()
}


//删除按钮
const delBtn=async (userId:string)=>{
    const confirm=await global.$confirmFunc("确定删除该数据吗?");//返回boolean
    if(confirm){
        let res=await delUser(userId)
        if(res&&res.code==200){
           ElMessage.success(res.msg) 
           list()
    }
}
}
const roleIds=ref([])
const roleIdJoin=ref('')
const roleList=async (userId:string)=>{
  let res=await getRoleList(userId);
  if(res&&res.code==200){
      roleIds.value=res.data;
      roleIdJoin.value=res.data.join(",")
}
}

const searchBtn=()=>{
    list()
}

const selected=(value:Array<string|number>)=>{
    console.log(value)
    addModel.roleId=value.join(',')
}

const tableList=ref([])
const list=async ()=>{
    let res=await getList(searchParam)
    if(res&&res.code==200){
        tableList.value=res.data.records;
        searchParam.total=res.data.total;
    }
}

//表单提交
const onCommit=()=>{
    addForm.value?.validate(async (valid)=>{
        if(valid){
            console.log("表单验证通过")
            //提交请求
            let res=null
            if(addModel.userId){
                res=await editUser(addModel)
            }else{
                res=await addUser(addModel)
            }
            if(res&&res.code==200){
                ElMessage.success(res.msg)
                onClose()
                list()
        }else{
            ElMessage.error(res.msg)
        }
          
    }
})
}

const resetBtn=()=>{
    searchParam.phone=''
     searchParam.nickName=''
    searchParam.currentPage=1
    list()
}


//查询角色下拉数据
const getSelect=async ()=>{
    let res = await getSelectList();
    if(res&&res.code==200){
        options.value=res.data;
    }
}

onMounted(()=>{
    nextTick(()=>{
        tableHeight.value=window.innerHeight-230
    })
   // getSelect()
    list()
})
</script>

<style scoped>

</style>