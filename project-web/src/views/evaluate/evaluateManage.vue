<template>
    <div class="min-h-screen bg-gray-50 flex">
    <div class="flex-1 py-6 px-8">
  
    <!-- 评价管理页面 -->
    <div  class="space-y-6">
    <!-- 统计概览 -->
    <div class="space-y-6">
    <div class="grid grid-cols-3 gap-4">
    <div class="bg-white p-6 rounded-lg shadow-sm">
    <h3 class="text-gray-500 mb-2">总评价数</h3>
    <div class="text-3xl font-bold">{{ statistics.totalReviews }}</div>
    <div class="mt-2 text-sm text-gray-500">本月新增: {{ statistics.monthlyReviews }}</div>
    </div>
    <div class="bg-white p-6 rounded-lg shadow-sm">
    <h3 class="text-gray-500 mb-2">平均评分</h3>
    <div class="flex items-center">
    <span class="text-3xl font-bold mr-2">{{ formatNumberToTwoDecimals(statistics.averageRating) }}</span>
    <el-rate v-model="statistics.averageRating" disabled />
    </div>
    <div class="mt-2 text-sm text-gray-500">满意度: {{ formatNumberToTwoDecimals(statistics.satisfactionRate) }}%</div>
    </div>
    <div class="bg-white p-6 rounded-lg shadow-sm">
    <h3 class="text-gray-500 mb-2">本月评分趋势</h3>
    <el-progress type="circle" :percentage="formatNumberToTwoDecimals(statistics.satisfactionRate)" :width="80" class="mt-2" />
    </div>
    </div>
    <div class="grid grid-cols-2 gap-4">
    <div class="bg-white p-6 rounded-lg shadow-sm">
    <h3 class="text-gray-500 mb-4">评分分布</h3>
    <div ref="ratingChart" class="h-64"></div>
    </div>
    <div class="bg-white p-6 rounded-lg shadow-sm">
    <div class="h-64">
        <div ref="ratingChartPie" style="width: 100%; height: 350px;"></div>
    </div>
    </div>
    </div>
    </div>
    <!-- 评价列表 -->
    <div class="bg-white rounded-lg shadow-sm">
    <div class="p-4 border-b">
    <div class="flex items-center space-x-4">
    <el-input
    v-model="searchParam.remark"
    placeholder="搜索评价内容"
    class="w-64"
    >
    <template #prefix>
    <el-icon><Search /></el-icon>
    </template>
    </el-input>
    <el-select v-model="searchParam.score" placeholder="评分筛选" class="w-32">
    <el-option
    v-for="item in ratingOptions"
    :key="item.value"
    :label="item.label"
    :value="item.value"
    />
    </el-select>
    <!-- <el-date-picker
    v-model="reviewDateRange"
    type="daterange"
    range-separator="至"
    start-placeholder="开始日期"
    end-placeholder="结束日期"
    /> -->
    <el-date-picker
        v-model="searchParam.startTime"
        type="date"
        placeholder="开始时间"
        value-format="YYYY-MM-DD"
      />
      <el-date-picker
        v-model="searchParam.endTime"
        type="date"
        placeholder="结束时间"
        value-format="YYYY-MM-DD"
      />
    <el-button type="primary" class="!rounded-button whitespace-nowrap" @click="getEvaluateList">
    搜索
    </el-button>
    <el-button type="warning" class="!rounded-button whitespace-nowrap" @click="resetBtn">
    重置
    </el-button>
    </div>
    </div>
    <div class="divide-y">
    <div v-for="review in reviewList" :key="review.id" class="p-6">
    <div class="flex items-start">
    <img src="../../assets/images/image.jpg" class="w-10 h-10 rounded-full" :alt="review.dataName" />
    <div class="ml-4 flex-1">
    <div class="flex items-center justify-between">
    <div>
    <span class="font-medium">{{ review.createName }}</span>
    <el-rate v-model="review.score" disabled class="ml-2" />
    </div>
    <span class="text-gray-500">{{ review.createTime }}</span>
    </div>
    <p class="mt-2 text-gray-600">{{ review.remark }}</p>
    <div class="mt-2 text-sm text-gray-500">
    评价数据集：{{ review.dataName }}
    </div>
    </div>
    </div>
    </div>
    </div>
    <!-- 评价分页 -->
    <div class="flex justify-center p-4 border-t">
    <el-pagination
    v-model:current-page="searchParam.currentPage"
    v-model:page-size="searchParam.pageSize"
    :total="searchParam.total"
    :page-sizes="[10, 20, 30, 40]"
    layout="total, sizes, prev, pager, next"
    />
    </div>
    </div>
    </div>
    </div>
    </div>
    </template>
    <script lang="ts" setup>
    import { ref, onMounted,reactive } from 'vue';
    import { Search} from '@element-plus/icons-vue';
    import {getEvaluateListApi,getChartApi} from '@/api/evaluate/index'
    import * as echarts from 'echarts';
    let chart: echarts.ECharts | null = null;

    // 搜索相关
    const searchParam=reactive({
    currentPage:1,
    pageSize:10,
    total:0,
    startTime:'',
    endTime:'',
    score:'',
    remark:'',
})

    // 评价管理相关
    const statistics = ref({
    totalReviews: 1280,
    averageRating: 4.6,
    monthlyReviews: 326,
    responseRate: 98.5,
	reviewTrend: [120, 145, 168, 182, 196, 224, 245, 268, 292, 308, 326],
    satisfactionRate: 94.2,
    scoreRate:0
    });
    const resetBtn=()=>{
    searchParam.currentPage=1;
    searchParam.pageSize=10;
    searchParam.total=0; 
    searchParam.startTime='';
    searchParam.endTime='';
    searchParam.score='';
    searchParam.remark='';
    getEvaluateList()
    }
    let evaTotal:any=[]
    const ratingOptions = [
    { label: '全部评分', value: '' },
    { label: '5星', value: 5 },
    { label: '4星', value: 4 },
    { label: '3星', value: 3 },
    { label: '2星', value: 2 },
    { label: '1星', value: 1 }
    ];
    // 评价列表
    const reviewList:any = ref([]);
    // 评分图表
    const ratingChart = ref<HTMLElement | null>(null);
    const ratingChartPie = ref<HTMLElement | null>(null);

    const targetData = ref([
    { value: 0, name: '1星', itemStyle: { color: '#ff6384' } },
    { value: 0, name: '2星', itemStyle: { color: '#ff9f40' } },
    { value: 1, name: '3星', itemStyle: { color: '#ffcd56' } },
    { value: 4, name: '4星', itemStyle: { color: '#4bc0c0' } },
    { value: 4, name: '5星', itemStyle: { color: '#36a2eb' } }
    ]);
    const initChart = () => {
    if (!ratingChartPie.value) return;
    chart = echarts.init(ratingChartPie.value);
  const option = {
    title: {
      text: '评价星级分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: ['1星', '2星', '3星', '4星', '5星']
    },
    series: [
      {
        name: '评价数量',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data:targetData.value
      }
    ]
  };

  chart.setOption(option);
};
    onMounted(async() => {
    await getEvaluateList()
    await  getChartData()
    initChart();
    if (ratingChart.value) {
    const chart = echarts.init(ratingChart.value);
    const option = {
    animation: false,
    tooltip: {
    trigger: 'axis',
    axisPointer: {
    type: 'shadow'
    }
    },
    grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
    },
    xAxis: {
    type: 'category',
    data: ['1星', '2星', '3星', '4星', '5星']
    },
    yAxis: {
    type: 'value',
    splitLine: {
    lineStyle: {
    type: 'dashed'
    }
    }
    },
    series: [
    {
    name: '评价数量',
    type: 'bar',
    data: evaTotal,
    itemStyle: {
    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: '#83bff6' },
    { offset: 0.5, color: '#409EFF' },
    { offset: 1, color: '#2d8cf0' }
    ])
    },
    emphasis: {
    itemStyle: {
    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: '#90c5f7' },
    { offset: 0.5, color: '#4dabff' },
    { offset: 1, color: '#3a99f1' }
    ])
    }
    },
    barWidth: '40%',
    showBackground: true,
    backgroundStyle: {
    color: 'rgba(180, 180, 180, 0.1)'
    }
    }
    ]
    };
    chart.setOption(option);
    window.addEventListener('resize', () => {
    chart.resize();
    });
    }
    });
   
    const formatNumberToTwoDecimals=(num:any)=> {
    return Number(num).toFixed(2);
}

    const getChartData = async() => {
        let res=await  getChartApi(searchParam)
           if(res&&res.code==200){
            statistics.value.totalReviews=res.data.totalEvaluate;
            statistics.value.averageRating=res.data.scoreRate;
            statistics.value.monthlyReviews=res.data.addTotal;
            statistics.value.satisfactionRate=res.data.satisfaction;
            evaTotal=res.data.evaTotal;
                // 拼接数据
                for (let i = 0; i < targetData.value.length; i++) {
                targetData.value[i].value = evaTotal[i];
                }

           
        }
    };
    

    const getEvaluateList = async() => {
        let res=await  getEvaluateListApi(searchParam)
           if(res&&res.code==200){
            searchParam.total=res.data.total;
            reviewList.value=res.data.records;
        }
    };
    </script>
    <style scoped>
    .el-tree {
    background: transparent;
    }
    input[type="number"]::-webkit-inner-spin-button,
    input[type="number"]::-webkit-outer-spin-button {
    -webkit-appearance: none;
    margin: 0;
    }
    .el-date-editor.el-input, .el-date-editor.el-input__wrapper {
    width: 300px !important;
    }
    </style>
    