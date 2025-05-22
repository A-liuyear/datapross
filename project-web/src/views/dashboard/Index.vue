<!-- 代码已包含 CSS：使用 TailwindCSS , 安装 TailwindCSS 后方可看到布局样式效果 -->
<template>
    <div class="min-h-screen bg-gray-50 flex">

    <!-- 主要内容区 -->
    <div class="flex-1 py-6 px-8">
    <div v-if="activeTab === 'browse'" class="space-y-6">
    <!-- 搜索区域 -->
    <div class="bg-white p-6 rounded-lg shadow-sm">
    <div class="flex items-center space-x-4 mb-4">
    <div class="flex-1 relative">
    <input
    type="text"
    v-model="searchQuery.searchText"
    class="w-full h-12 pl-12 pr-4 border-2 border-gray-200 rounded-lg focus:border-blue-500 focus:outline-none text-sm"
    placeholder="搜索数据集..."
    />
    <el-icon class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">
    <Search />
    </el-icon>
    </div>
    <el-button type="primary" class="h-12 !rounded-button whitespace-nowrap" @click="searchRemark">高级搜索</el-button>
    </div>
    
    </div>
    <!-- 主体内容 -->
    <div class="flex space-x-6">
    <!-- 左侧筛选面板 -->
    <div class="w-64 bg-white p-4 rounded-lg shadow-sm space-y-6">
    <div>
    <h3 class="font-medium mb-3">数据分类</h3>
    <el-tree :data="categoryTree" :props="defaultProps" @node-click="handleNodeClick" />
    </div>
    <div>
    <h3 class="font-medium mb-3">价格区间</h3>
    <el-slider
    v-model="priceRange"
    range
    :min="0"
    :max="1000"
    :step="100"
    />
    <div class="text-sm text-gray-500 mt-2">
    {{ priceRange[0] }}元 - {{ priceRange[1] }}元
    </div>
    </div>
    <div>
    <h3 class="font-medium mb-3">发布开始日期</h3>
    <el-date-picker
    v-model="searchQuery.dateStart"
    type="date"
    start-placeholder="开始日期"
    size="default"
    class="w-full !border-gray-200"
    :shortcuts="[
    {
    text: '最近一周',
    value: () => {
    const end = new Date();
    const start = new Date();
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
    return [start, end];
    },
    },
    {
    text: '最近一个月',
    value: () => {
    const end = new Date();
    const start = new Date();
    start.setMonth(start.getMonth() - 1);
    return [start, end];
    },
    },
    {
    text: '最近三个月',
    value: () => {
    const end = new Date();
    const start = new Date();
    start.setMonth(start.getMonth() - 3);
    return [start, end];
    },
    },
    ]"
    />
    <h3 class="font-medium mb-3">发布结束日期</h3>
    <el-date-picker
    v-model="searchQuery.dateEnd"
    type="date"
    end-placeholder="结束日期"
    size="default"
    class="w-full !border-gray-200"
    :shortcuts="[
    {
    text: '最近一周',
    value: () => {
    const end = new Date();
    const start = new Date();
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
    return [start, end];
    },
    },
    {
    text: '最近一个月',
    value: () => {
    const end = new Date();
    const start = new Date();
    start.setMonth(start.getMonth() - 1);
    return [start, end];
    },
    },
    {
    text: '最近三个月',
    value: () => {
    const end = new Date();
    const start = new Date();
    start.setMonth(start.getMonth() - 3);
    return [start, end];
    },
    },
    ]"
    />
    </div>
    <div>
    <h3 class="font-medium mb-3">评分筛选</h3>
    <el-rate v-model="searchQuery.rating" />
    </div>
    <div class="flex space-x-4 pt-4">
    <el-button class="flex-1 !rounded-button" @click="resetFilters">重置</el-button>
    <el-button type="primary" class="flex-1 !rounded-button" @click="applyFilters">确定</el-button>
    </div>
    </div>
    <!-- 右侧数据列表 -->
    <div class="flex-1 space-y-4">
    <div class="bg-white p-4 rounded-lg shadow-sm flex justify-between items-center">
    <div class="flex space-x-4">
    <el-button-group>
    <el-button
    :type="viewMode === 'list' ? 'primary' : 'default'"
    class="!rounded-button"
    @click="viewMode = 'list'"
    >
    <el-icon><List /></el-icon>
    </el-button>
    <el-button
    :type="viewMode === 'grid' ? 'primary' : 'default'"
    class="!rounded-button"
    @click="viewMode = 'grid'"
    >
    <el-icon><Grid /></el-icon>
    </el-button>
    </el-button-group>
    </div>
    <el-select v-model="searchQuery.sortBy" placeholder="排序方式" class="w-32" @change="searchRemark">
    <el-option label="最多评价" value="most_reviews" />
    <el-option label="评分最高" value="highest_rated" />
    </el-select>
    </div>
    <!-- 数据集列表 -->
    <div :class="viewMode === 'grid' ? 'grid grid-cols-3 gap-4' : 'space-y-4'">
    <div v-for="item in datasetList" :key="item.dataId"
    class="bg-white rounded-lg shadow-sm overflow-hidden"
    style="cursor: pointer;"
    :class="viewMode === 'grid' ? '' : 'flex'"
    @click="goToPage2(item.dataId)"
    >
    <div :class="viewMode === 'grid' ? 'h-48' : 'w-48 h-32'" class="relative overflow-hidden">
    <img :src="item.imageUrl" class="w-full h-full object-cover" :alt="item.fileName" />
    </div>
    <div class="p-4 flex-1">
    <h3 class="text-lg font-medium mb-2">{{ item.fileName }}</h3>
    <p class="text-gray-600 text-sm mb-2 line-clamp-2">{{ item.remark }}</p>
    <div class="flex items-center justify-between">
    <div class="flex items-center space-x-4">
    <el-rate v-model="item.avgScore" disabled />
    <span class="text-sm text-gray-500">({{ item.evateCount }}评价)</span>
    </div>
    <div class="text-lg font-medium text-blue-600">¥{{ item.price }}</div>
    </div>
    </div>
    </div>
    </div>
    <!-- 分页 -->
    <div class="flex  py-4">
    <el-pagination
    v-model:current-page="currentPage"
    v-model:page-size="pageSize"
    :total="total"
    :page-sizes="[3, 6, 9, 12]"
    layout="total, sizes, prev, pager, next"
     @size-change="sizeChange"
    @current-change="currentChange"
    />
    </div>
    </div>
    </div>
    </div>
    </div>
    </div>
    </template>
    <script lang="ts" setup>
    import { ref, onMounted } from 'vue';
    import { Search, List, Grid } from '@element-plus/icons-vue';
    import {getList} from '@/api/data/index'
    import { useRouter } from 'vue-router'
    // 页面切换
    const activeTab = ref('browse');


    const router = useRouter()
   
    // 搜索相关
    const searchQuery = ref({
        dataId:'',
        fileName:'',
        searchText:'',
        priceMin:'',
        priceMax:'',
        dateStart: '', // 日期范围
        dateEnd: '', // 日期范围
        rating:'', // 评分
        category:'', // 分类
        sortBy:'',
        currentPage:1,
        pageSize:3,
        total:0,
        from:'index'

    })

    // 筛选相关
    const categoryTree = ref([
  {
    label: '金融数据',
    children: [
      { label: '股票数据' },
      { label: '基金数据' },
      { label: '期货数据' },
      { label: '外汇数据' },
      { label: '债券数据' },
      { label: '数字货币' },
      { label: '宏观经济指标' }
    ]
  },
  {
    label: '用户行为',
    children: [
      { label: '点击流数据' },
      { label: '购买记录' },
      { label: '浏览历史' },
      { label: '搜索关键词' },
      { label: 'APP使用时长' },
      { label: '社交互动数据' }
    ]
  },
  {
    label: '企业运营',
    children: [
      { label: '财务报表' },
      { label: '供应链数据' },
      { label: '库存管理' },
      { label: '人力资源数据' },
      { label: '生产制造数据' },
      { label: '客户服务记录' }
    ]
  },
  {
    label: '市场分析',
    children: [
      { label: '竞品数据' },
      { label: '行业报告' },
      { label: '广告投放效果' },
      { label: '市场份额数据' },
      { label: '价格监测' },
      { label: '消费者调研' }
    ]
  },
  {
    label: '物联网数据',
    children: [
      { label: '传感器数据' },
      { label: '设备运行日志' },
      { label: 'GPS定位数据' },
      { label: '智能家居数据' },
      { label: '工业机器数据' }
    ]
  },
  {
    label: '公共数据',
    children: [
      { label: '政府公开数据' },
      { label: '天气气候数据' },
      { label: '交通流量数据' },
      { label: '医疗健康数据' },
      { label: '教育统计' }
    ]
  }
]);
    const defaultProps = {
    children: 'children',
    label: 'label'
    };
    const priceRange = ref([0, 10000]);
    const ratingFilter = ref(0);
    const viewMode = ref('grid');
    // 数据集列表
    const datasetList:any = ref([]);
    // 分页相关
    const currentPage = ref(1);
    const pageSize = ref(3);
    const total = ref(0);
    // 评价管理相关
   
    // 评分图表
    onMounted(() => {
    searchRemark()
    });
    
    const handleNodeClick = (data: any) => {
        searchQuery.value.category=data.label;
        searchRemark()
    };
    const resetFilters = () => {
    priceRange.value = [0, 1000];
    searchQuery.value.dateStart = ''; // 清空日期范围
    searchQuery.value.dateEnd = ''; // 清空日期范围
    searchQuery.value.category = ''; // 清空分类
    searchQuery.value.rating = ''; // 清空评分
    ratingFilter.value = 0;
    searchQuery.value.searchText = ''; // 清空评分
    searchRemark()
    };
    const applyFilters = () => {
      searchRemark()
    };
    const sizeChange=(size:number)=>{
    searchQuery.value.pageSize=size;
    searchRemark()
}

const currentChange=(page:number)=>{
    searchQuery.value.currentPage=page;
    searchRemark()
}


    const goToPage2 = (id:number) => {
        router.push({path:'/evaluateAdd',
            query:{
                id:id
               }
          }
        ) // 跳转到 /page2
        }


   const searchRemark =async()=>{
        searchQuery.value.priceMin=priceRange.value[0]+''
        searchQuery.value.priceMax=priceRange.value[1]+''
       const res= await getList(searchQuery.value) // 调用接口获取数据
       if (res.code === 200) {
           datasetList.value=res.data.records;
           currentPage.value=res.data.current
           total.value=res.data.total;
       }
   }
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
    </style>
    