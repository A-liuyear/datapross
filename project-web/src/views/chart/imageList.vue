<!-- 代码已包含 CSS：使用 TailwindCSS , 安装 TailwindCSS 后方可看到布局样式效果 -->

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="container mx-auto px-4 py-8">
      <div class="grid grid-cols-2 gap-8">
        <div v-for="(item, index) in tableList" :key="index" class="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300">
          <div class="relative h-[400px] cursor-pointer overflow-hidden" @click="openImageViewer(item)">
            <img :src="item.imageUrl" :alt="item.content" class="w-full h-full object-cover object-top transition-transform duration-300 hover:scale-105">
          </div>
          <div class="p-6">
            <p class="text-gray-600 leading-relaxed">{{ item.content }}</p>
            <br>
            <p class="text-gray-600 leading-relaxed">{{ item.createTime }}</p>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="mt-8 flex justify-center">
        <el-pagination
          v-model:current-page="searchQuery.currentPage"
          :page-size="searchQuery.pageSize"
          :total="searchQuery.total"
          :background="true"
          layout="prev, pager, next"
          @current-change="currentChange"
        />
      </div>
    </div>

    <!-- 图片查看器 -->
    <el-dialog
      v-model="imageViewerVisible"
      :show-close="true"
      width="80%"
      class="image-viewer-dialog"
    >
      <div class="relative">
        <img
          :src="selectedImage?.imageUrl"
          :alt="selectedImage?.content"
          class="w-full h-full object-contain"
        >
      </div>
      <div class="mt-4 text-center text-gray-800">
        <p>{{ selectedImage?.content }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
  import { ref,onMounted } from 'vue';
  import {getMyImagesApi} from '@/api/echarts/index';
interface ImageItem {
  imageUrl: string;
  content: string;
}


const imageViewerVisible = ref(false);
const selectedImage = ref<ImageItem | null>(null);



const openImageViewer = (item: ImageItem) => {
  selectedImage.value = item;
  imageViewerVisible.value = true;
};



const tableList:any=ref([])
  const getMyImagesList=async()=>{
    let res=await  getMyImagesApi(searchQuery.value)
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

const currentChange=(page:number)=>{
    searchQuery.value.currentPage=page;
    getMyImagesList()
  }
  onMounted(async() => {
    getMyImagesList()
  })
</script>

<style scoped>
.image-viewer-dialog :deep(.el-dialog__header) {
  display: none;
}

.image-viewer-dialog :deep(.el-dialog__body) {
  padding: 20px;
}
</style>

