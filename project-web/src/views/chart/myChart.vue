<template>
   <div class="min-h-screen bg-gray-50 py-8">
    <div class="mx-auto max-w-7xl px-6">
      <h1 class="mb-8 text-2xl font-bold text-gray-900">数据分析面板</h1>
      <div class="grid grid-cols-2 gap-6">
        <div v-for="item in tableList" 
             :key="item.id" 
             class="chart-container bg-white rounded-lg shadow-md p-4 transition-all duration-300 hover:shadow-lg">
          <DynamicChart 
            v-if="item.genChart" 
            :chart-data="item.genChart" 
            :title="item.chartType" 
            :data="item.createTime"
            class="w-full h-[320px]"
          />
        </div>
       
      </div>

      <div class="flex justify-center mt-8">
        <el-pagination
          v-model:current-page="searchQuery.currentPage"
          :page-size="searchQuery.pageSize"
          :total="searchQuery.total"
          :page-sizes="[6, 9, 12]"
          layout="total, sizes, prev, pager, next"
          @size-change="sizeChange"
          @current-change="currentChange"
        />
      </div>
    </div>
  </div>
  </template>
  
  <script lang="ts" setup>
  import { ref,onMounted } from 'vue';
 import {getMyChartApi} from '@/api/echarts/index';
 import DynamicChart from '@/components/DynamicChart.vue';


  const tableList:any=ref([])
  const getMyChartList=async()=>{
    let res=await  getMyChartApi(searchQuery.value)
    if(res&&res.code==200){
      tableList.value=res.data.records;
      searchQuery.value.total=res.data.total;
    }
  }
  const searchQuery = ref({
        currentPage:1,
        pageSize:6,
        total:0

    })
  const sizeChange=(size:number)=>{
    searchQuery.value.pageSize=size;
    getMyChartList()
}

const currentChange=(page:number)=>{
    searchQuery.value.currentPage=page;
    getMyChartList()
  }
  onMounted(async() => {
    getMyChartList()
  })
  </script>
  
  <style scoped>
  .el-upload {
    width: 100%;
  }
  
  .el-upload-dragger {
    width: 100%;
  }
  
  :deep(.el-form-item__label) {
    font-weight: 500;
  }
  .dashboard {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 24px;
  padding: 20px;
}
  </style>
  
  