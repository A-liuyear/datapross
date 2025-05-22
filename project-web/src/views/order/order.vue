<template>
    <el-main height="">
        <el-form :model="searchParam"  :inline="true" size="default">
            <el-form-item >
                <el-input placeholder="请输入文件名称" v-model="searchParam.dataName"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button  icon="Search"  @click="searchBtn">搜索</el-button>
                <el-button  icon="Close" plain type="danger" @click="resetBtn">重置</el-button>
                <el-button  icon="Download" plain type="success" @click="exportExcelFunc">导出</el-button>
            </el-form-item>
        </el-form>

         <!--表格-->
         <el-table :height="tableHeight" :data="tableList" border stripe >
            <el-table-column type="index" width="100" label="序号" />
            <el-table-column prop="dataName" label="文件名称"></el-table-column>
            <el-table-column prop="payAmount" label="积分"></el-table-column>
            <el-table-column prop="pointType" label="支出/赚取"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="updateTime" label="更新时间"></el-table-column>
            <el-table-column  label="操作" width="320" align="center" v-if="global.$hasPerm(['sys:order:download'])">
                <template #default="scope">
                    <el-button v-if="global.$hasPerm(['sys:order:download'])"  type="primary" icon="Edit" size="default" @click="download(scope.row)">
                     下载
                    </el-button>

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
    </el-main>
    
</template>

<script setup lang="ts">
import { onMounted, reactive,ref ,nextTick} from 'vue';
import {getList,downLoadExcelApi,exportExcel} from '@/api/order/index'
import useInstance from '@/hooks/useInstance'
//获取全局global
const {global}=useInstance()
   
const searchParam=reactive({
    dataName:'',
    currentPage:1,
    pageSize:10,
    total:0
})


//表格高度
const tableHeight=ref(0)


const sizeChange=(size:number)=>{
    searchParam.pageSize=size;
    list()
}

const currentChange=(page:number)=>{
    searchParam.currentPage=page;
    list()
}

const download=(row:any)=>{
    downLoadExcelApi(row.dataId,row.dataName).then(res=>{
        console.log(res)
    })
}

const exportExcelFunc=()=>{
    exportExcel()
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
    searchParam.dataName=''
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