<!-- 代码已包含 CSS：使用 TailwindCSS , 安装 TailwindCSS 后方可看到布局样式效果 -->
<template>
    <div class="min-h-screen">

    <div class="container mx-auto px-4">
    <!-- 基本信息区 -->
    <div class="bg-white rounded-lg p-6 mb-6">
    <div class="flex justify-between items-start">
    <div class="flex-1">
    <h1 class="text-2xl font-bold mb-4">{{model.fileName}}</h1>
    <div class="flex items-center gap-4 mb-4">
    <el-tag>{{model.category}}</el-tag>
    <el-tag type="success">实时更新</el-tag>
    </div>
    <div class="flex items-center gap-2 text-gray-600 mb-4">
    <el-icon><Calendar /></el-icon>
    <span>更新时间: {{model.updateTime}}</span>
    <span class="mx-4">|</span>
    <el-icon><DataLine /></el-icon>
    <span>数据量: {{model.dataTotal}} 条</span>
    </div>
    <div class="flex items-center gap-4">
    <el-rate v-model="model.rating" disabled show-score />
    <span class="text-gray-500">({{ model.reviewCount }}条评价)</span>
    </div>
    </div>
    <div class="text-center">
    <div class="text-3xl font-bold text-blue-600 mb-2">¥{{ model.price }}</div>
    
    <el-button v-show="!showFlag" type="primary" size="large" class="!rounded-button whitespace-nowrap" @click="payData">立即购买</el-button>
    <el-button v-show="showFlag" type="success" size="large" class="!rounded-button whitespace-nowrap" >已经购买</el-button>
    </div>
    </div>
    </div>
    <!-- 评价区域 -->
    <div class="bg-white rounded-lg p-6">
    <h2 class="text-xl font-bold mb-6">用户评价</h2>
    <div class="flex items-start gap-8 mb-8">
    <div class="text-center">
    <div class="text-3xl font-bold text-blue-600 mb-2">{{formatNumberToTwoDecimals(overallRating)}}</div>
    <el-rate v-model="overallRating" disabled />
    </div>
    <div class="flex-1">
    <div v-for="(item, index) in ratingDistribution" :key="index" class="flex items-center gap-4 mb-2">
    <span class="w-16">{{ 5 - index }}星</span>
    <div class="flex-1 bg-gray-200 rounded-full h-2">
    <div class="bg-blue-600 h-full rounded-full" :style="{ width: item + '%' }"></div>
    </div>
    <span class="w-16 text-right">{{ formatNumberToTwoDecimals(item) }}%</span>
    </div>
    </div>
    </div>
    <!-- 评价入口 -->
    <div class="mb-6">
    <el-button type="primary" class="!rounded-button whitespace-nowrap" @click="showReviewDialog = true">
    <el-icon class="mr-1"><EditPen /></el-icon>
    发表评价
    </el-button>
    </div>
    <!-- 评价对话框 -->
    <el-dialog
    v-model="showReviewDialog"
    title="发表评价"
    width="500px"
    >
    <div class="mb-4">
    <div class="mb-2">评分</div>
    <el-rate v-model="newReview.score" />
    </div>
    <div class="mb-4">
    <div class="mb-2">评价内容</div>
    <el-input
    v-model="newReview.remark"
    type="textarea"
    :rows="4"
    placeholder="请输入您的评价内容..."
    maxlength="500"
    show-word-limit
    />
    </div>
    <template #footer>
    <span class="dialog-footer">
    <el-button @click="showReviewDialog = false">取消</el-button>
    <el-button type="primary" @click="submitReview" class="!rounded-button whitespace-nowrap">提交评价</el-button>
    </span>
    </template>
    </el-dialog>
    <!-- 评价列表 -->
    <div class="border-t pt-6">
    <div v-for="review in reviews" :key="review.id" class="mb-6 pb-6 border-b last:border-b-0">
    <div class="flex items-center gap-4 mb-2" >
    <img src="../../assets/images/image.jpg" style="width: 50px;height: 50px; border-radius: 100%"/>
    <div>
    <div class="font-medium">{{ review.createName }}</div>
    <el-rate v-model="review.score" disabled />
    </div>
    <div class="text-gray-500 ml-auto">{{ review.createTime }}</div>
    </div>
    <p class="text-gray-700">{{ review.remark }}</p>
    </div>
    </div>
    <!-- 分页 -->
    <div class="flex justify-center mt-6">
    <el-pagination
    v-model:current-page="searchParam.currentPage"
    :page-size="searchParam.pageSize"
    :total="searchParam.total"
    layout="prev, pager, next"
    />
    </div>
    </div>
    </div>
    </div>
    </template>
    <script lang="ts" setup>
    import { ref, onMounted,reactive } from 'vue';
    import { ElMessage } from 'element-plus';
    import { Calendar, DataLine, EditPen } from '@element-plus/icons-vue';
    import {getById,payDataApi } from '@/api/data/index'
    import {addEvaluate,getEvaluateListApi,getScoreRateApi} from '@/api/evaluate/index'
    import useInstance from '@/hooks/useInstance'
  
    import { useRoute } from 'vue-router';
    const overallRating = ref(0);
    const showReviewDialog = ref(false);
    const newReview = ref({
    dataId:'',
    score: 5,
    remark: ''
    });

 const formatNumberToTwoDecimals=(num:any)=> {
    return Number(num).toFixed(2);
}

    const searchParam=reactive({
    dataId:'',
    currentPage:1,
    pageSize:10,
    total:0
})
    const {global}=useInstance()
    const route = useRoute();
  // 搜索相关
  const model = ref({
        dataId:'',
        fileName:'',
        searchText:'',
        price:'',
        category:'', // 分类
        sortBy:'',
        updateTime:'',
        dataTotal:0,
        reviewCount:0, // 评价数量
        reviewAvg:0, // 评价平均分
            rating: '0', // 总体评分
            count: 0 // 评价数量
    })
 const Order=ref({
    dataId:'',
    payAmount:''
 })

    const submitReview = async() => {
    if (!newReview.value.remark.trim()) {
    ElMessage.warning('请输入评价内容');
    return;
    }else{
        const id= route.query.id as string;
        newReview.value.dataId=id;
        let res=await  addEvaluate(newReview.value)
           if(res&&res.code==200){
            ElMessage.success('评价成功');
            getEvaluateList()
            getScoreRateList()
        }
    }
    showReviewDialog.value = false;
    newReview.value.remark = '';
    newReview.value.score = 5;
    };
    const getEvaluateList = async() => {
        searchParam.dataId= route.query.id as string;

        let res=await  getEvaluateListApi(searchParam)
           if(res&&res.code==200){
            searchParam.total=res.data.total;
            reviews.value=res.data.records;
        }
    };

    const ratingDistribution = ref([0, 0, 0, 0, 0]);

    const getScoreRateList = async() => {
        let res=await  getScoreRateApi({dataId:route.query.id})
           if(res&&res.code==200){
            ratingDistribution.value=res.data.dataList;
            overallRating.value=res.data.scoreRate;
        }
    };


   
    const reviews:any = ref([]);
    const showFlag=ref(false)
    const payData = async() => {
        const confirm=await global.$confirmFunc("确定购买该数据吗?");//返回boolean
        if(confirm){
            Order.value.dataId=model.value.dataId;
            Order.value.payAmount=model.value.price;
            let res=await  payDataApi(Order.value)
            if(res&&res.code==200){
                showFlag.value=true;
              ElMessage.success(res.msg);
            }else{
              ElMessage.error(res.msg);
            }


    }
       
    };



    onMounted(async() => {
        getEvaluateList()
        getScoreRateList()
    const id = route.query.id==null?'':route.query.id;
   
    let res=await  getById(id)
    if(res&&res.code==200){
        model.value=res.data.obj;
        model.value.reviewCount=res.data.evaluateTotal
        model.value.rating=formatNumberToTwoDecimals(res.data.scoreRate)
        showFlag.value=res.data.obj.showFlag=='0'?false:true;
    }
    
    });
    </script>
    <style scoped>
 
    </style>
    