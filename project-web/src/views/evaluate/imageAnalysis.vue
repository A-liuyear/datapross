<!-- 代码已包含 CSS：使用 TailwindCSS , 安装 TailwindCSS 后方可看到布局样式效果 -->

<template>
  <div class=" bg-white">
    <div class="mx-auto" style="width: 1440px;">
      <!-- 顶部提示 -->
      <div class="bg-blue-50 p-4 mb-6">
        <div class="flex items-center text-blue-600">
          <el-icon class="mr-2"><Warning /></el-icon>
          <span>接口限制原因，请填写正确图片地址，否则会导致生成失败。(例如：https://images.unsplash.com/photo-1506744038136-46273834b3fb)</span>
        </div>
      </div>

      <!-- 主体内容 -->
      <div class="flex gap-6 px-6 max-h-[500px]">
        <!-- 左侧表单 -->
        <div class="w-[600px] bg-white rounded-lg p-6 border border-gray-200">
          <h2 class="text-xl font-bold mb-6">图片分析</h2>
          
          <el-form :model="form" label-position="top" :disabled="formLoading">

            <el-form-item label="原始图片链接">
              <el-input
                v-model="form.fileUrl"
                style="width: 480px"
                :rows="5"
                type="textarea"
                placeholder="请输入链接地址"
              />
           
            <div class="flex gap-4 mt-8" style="margin-bottom: 100px;">
              <el-button 
                type="primary" 
                @click="handleSubmit"
                 :loading="formLoading"
                class="flex-1 !rounded-button whitespace-nowrap"
              >
                提交
              </el-button>
              <el-button 
                @click="handleReset"
                class="flex-1 !rounded-button whitespace-nowrap"
              >
                重置
              </el-button>
            </div>
            </el-form-item>
            <div style="margin-top: -90px;">
                <img :src="form.fileUrl" v-if="form.fileUrl" alt="Loading" style="width: 150px; height: 150px;">
              </div>
          
          </el-form>
        </div>

        <!-- 右侧结果展示 -->
        <div class="flex-1 space-y-6">
          <div class="bg-white rounded-lg p-6 border border-gray-200">
            <h3 class="text-lg font-medium mb-4">分析结论</h3>
            <div class="min-h-[400px] text-gray-500" id="resultContent">
              请完成左侧表单提交后查看分析结论
            </div>
          </div>
        </div>
      </div>

      <!-- 页脚 -->
      <footer class="flex justify-between items-center py-6 px-6 text-gray-400 mt-8">
        <a href="https://github.com" target="_blank" class="flex items-center hover:text-gray-600">
          <el-icon class="mr-2"><Link /></el-icon>
        </a>
      </footer>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { Warning, Link } from '@element-plus/icons-vue';
import {uploadImg} from '@/api/data/index'
import  { ElMessage  } from 'element-plus';
const formLoading = ref(false)


const form:any = ref({
  fileUrl: ''
});

// const handleFileChange = (file:any) => {
//   form.value.file = file.raw;
// };
//https://images.pexels.com/photos/730547/pexels-photo-730547.jpeg

const handleSubmit = async() => {
  if (!form.value.fileUrl) {
      ElMessage.warning('请填写图片地址');
      return;
  }

  formLoading.value = true
  let res=await uploadImg({fileUrl:form.value.fileUrl})
  if(res&&res.code==200){
    ElMessage.success(res.msg) 
    document.getElementById("resultContent")!.innerHTML =res.data.content;
  }
  formLoading.value = false
};

const handleReset = () => {
  form.value = {
    fileUrl: null
  };
};


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
</style>

