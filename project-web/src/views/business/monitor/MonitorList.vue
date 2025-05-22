<template>
    <el-main height="">
        <el-form :model="searchParam"  :inline="true" size="default">
            <el-form-item >
                <el-input placeholder="请输入机器编码" v-model="searchParam.mechineCode"></el-input>
            </el-form-item>
         
            <el-form-item>
                <el-button  icon="Search"  @click="searchBtn">搜索</el-button>
                <el-button  icon="Close" plain type="danger" @click="resetBtn">重置</el-button>
            </el-form-item>
        </el-form>

         <!--表格-->
         <el-table :height="tableHeight" :data="tableList" border stripe >
            <el-table-column type="index" width="50" label="序号" />
            <el-table-column prop="mechineCode" label="机器编码"></el-table-column>
            <el-table-column prop="tem" label="温度（℃）"></el-table-column>
            <el-table-column prop="hum" label="湿度（%）"></el-table-column>

            <el-table-column prop="status" label="状态">
                <template #default="scope">
                        <el-tag v-if="scope.row.status=='0'"  type="success" size="default" effect="dark" >正常</el-tag>
                        <el-tag v-if="scope.row.status=='1'"  type="danger" size="default" effect="dark" >异常</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="temLevel" label="温度状况">
                <template #default="scope">
                       <el-tag v-if="scope.row.temLevel=='1'"  type="danger" size="default" effect="dark" >高温</el-tag>
                        <el-tag v-if="scope.row.temLevel=='0'"  type="success" size="default" effect="dark" >正常</el-tag>
                        <el-tag v-if="scope.row.temLevel=='-1'"  type="info" size="default" effect="dark" >低温</el-tag>
                </template>
            </el-table-column>

            <el-table-column prop="humLevel" label="湿度状况">
                <template #default="scope">
                       <el-tag v-if="scope.row.humLevel=='1'"  type="danger" size="default" effect="dark" >高湿度</el-tag>
                        <el-tag v-if="scope.row.humLevel=='0'"  type="success" size="default" effect="dark" >正常</el-tag>
                        <el-tag v-if="scope.row.humLevel=='-1'"  type="info" size="default" effect="dark" >低湿度</el-tag>
                </template>
            </el-table-column>

            <el-table-column prop="createTime" label="创建时间"></el-table-column>
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
    </el-main>
    
</template>

<script setup lang="ts">
import { onMounted, reactive,ref ,nextTick} from 'vue';
import {getList} from '@/api/monitor/index'

const searchParam=reactive({
    mechineCode:'',
    currentPage:1,
    pageSize:10,
    total:0
})

//表格高度
const tableHeight=ref(0)




const sizeChange=(size:number)=>{
    searchParam.pageSize=size;
    //getList(searchParam)
    list()
}

const currentChange=(page:number)=>{
    searchParam.currentPage=page;
    //getList(searchParam)
    list()
}



const searchBtn=()=>{
    list()
}



const tableList=ref([])

const list=async ()=>{
    let res=await getList(searchParam)
    if(res&&res.code==200){
        tableList.value=res.data.records; 
        searchParam.total=res.data.total;
    }
}


const resetBtn=()=>{
    searchParam.mechineCode=''
    searchParam.currentPage=1
    list()
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