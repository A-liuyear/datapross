<template>
  <div class="min-h-screen bg-white">
    <div class="mx-auto" style="width: 1440px;">
      <!-- 顶部提示 -->
      <div class="bg-blue-50 p-4 mb-6">
        <div class="flex items-center text-blue-600">
          <el-icon class="mr-2"><Warning /></el-icon>
          <span>请保证输入的分析目标贴合原始数据集，否则会导致生成失败。</span>
        </div>
      </div>

      <!-- 主体内容 -->
      <div class="flex gap-6 px-6 min-h-[800px]">
        <!-- 左侧表单 -->
        <div class="w-[600px] bg-white rounded-lg p-6 border border-gray-200">
          <h2 class="text-xl font-bold mb-6">数据分析</h2>
          
          <el-form :model="form" label-position="top" :disabled="formLoading">
            <el-form-item label="分析目标" required>
              <el-input
                v-model="form.goal"
                type="textarea"
                :rows="4"
                placeholder="请输入分析需求，比如：分析网站用户的浏览偏好"
                class="!rounded-button"
              />
            </el-form-item>

            <el-form-item label="图表名称">
              <el-input 
                v-model="form.name"
                placeholder="请输入图表名称"
                class="!rounded-button"
              />
            </el-form-item>

            <el-form-item label="图表类型">
              <el-select 
                v-model="form.chartType"
                placeholder="请选择图表类型"
                class="w-full !rounded-button"
              >
                <el-option label="柱状图" value="柱状图" />
                <el-option label="雷达图" value="雷达图" />
                <el-option label="折线图" value="折线图" />
                <el-option label="饼图" value="饼图" />
                <el-option label="玫瑰图" value="玫瑰图" />
              </el-select>
            </el-form-item>

            <el-form-item label="原始数据">
              <el-select
                v-model="form.dataId"
                placeholder="请选择数据源"
                class="w-full !rounded-button"
                @change="handleDataChange"
              >
                <el-option
                  v-for="item in options"
                  :key="item.dataId"
                  :label="item.fileName"
                  :value="item.dataId"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="选择分析列" v-if="availableColumns.length > 0">
              <el-select
                v-model="form.selectedColumns"
                multiple
                placeholder="请选择要分析的列"
                class="w-full !rounded-button"
              >
                <el-option
                  v-for="column in availableColumns"
                  :key="column"
                  :label="column"
                  :value="column"
                />
              </el-select>
              <div class="text-xs text-gray-500 mt-1">可多选，不选则默认使用所有列</div>
            </el-form-item>

            <div class="flex gap-4 mt-8">
              <el-button 
                type="primary" 
                @click="handleSubmit"
                class="flex-1 !rounded-button whitespace-nowrap"
                :loading="formLoading"
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
          </el-form>
        </div>

        <!-- 右侧结果展示 -->
        <div class="flex-1 space-y-6">
          <div class="bg-white rounded-lg p-6 border border-gray-200">
            <h3 class="text-lg font-medium mb-4">分析结论</h3>
            <div class="min-h-[200px] text-gray-500" id="resultContent">
              请完成左侧表单提交后查看分析结论
            </div>
          </div>

          <div class="bg-white rounded-lg p-6 border border-gray-200">
            <h3 class="text-lg font-medium mb-4">可视化图表</h3>
            <div class="min-h-[400px] text-gray-500">
              请完成左侧表单提交后查看可视化图表
              <div class="dashboard">
                <!-- 方式1：直接传递数据 -->
                <DynamicChart v-if="chartData" :chart-data="chartData" :title="form.chartType" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 页脚 -->
      <footer class="flex justify-between items-center py-6 px-6 text-gray-400 mt-8">
        <a href="https://github.com" target="_blank" class="flex items-center hover:text-gray-600">
          <el-icon class="mr-2"><Link /></el-icon>
          灵感81
        </a>
        <div>© 2025 C1own</div>
      </footer>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { Warning, Link } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { getMyDataApi, generateChart, getDataColumns } from '@/api/data/index';
import DynamicChart from '@/components/DynamicChart.vue';

interface FormState {
  goal: string;
  name: string;
  chartType: string;
  dataId: string;
  selectedColumns: string[];
}

const formLoading = ref(false);
const form = ref<FormState>({
  goal: '',
  name: '',
  chartType: '',
  dataId: '',
  selectedColumns: []
});

const options = ref<any[]>([]);
const availableColumns = ref<string[]>([]);
const chartData = ref<any>();

// 获取数据源列名
const fetchDataColumns = async (dataId: string) => {
  try {
    const res = await getDataColumns(dataId);
    if (res && res.code === 200) {
      availableColumns.value = res.data || [];
    } else {
      availableColumns.value = [];
      ElMessage.warning('获取数据列失败');
    }
  } catch (err) {
    console.error('获取数据列失败:', err);
    availableColumns.value = [];
  }
};

// 数据源变化时获取列名
const handleDataChange = (dataId: string) => {
  if (dataId) {
    fetchDataColumns(dataId);
  } else {
    availableColumns.value = [];
  }
  form.value.selectedColumns = [];
};

const handleSubmit = async () => {
  formLoading.value = true;
  try {
    if (!form.value.goal) {
      ElMessage.warning('请输入分析目标');
      return;
    }
    if (!form.value.chartType) {
      ElMessage.warning('请选择图表类型');
      return; 
    }
    if (!form.value.dataId) {
      ElMessage.warning('请选择数据源');
      return; 
    }
    if (!form.value.name) {
      ElMessage.warning('请输入图表名称');
      return; 
    }

    const params = {
      ...form.value,
      // 如果用户没有选择任何列，则传空数组，后端会处理为使用所有列
      selectedColumns: form.value.selectedColumns || []
    };

    let res = await generateChart(params);
    if (res && res.code == 200) {
      ElMessage.success(res.msg);
      document.getElementById("resultContent")!.innerHTML = res.data.genResult;
      chartData.value = res.data.genChart;
    } else {
      ElMessage.error(res.msg);
    }
  } catch (err) {
    console.error("Error:", err);
    ElMessage.error('生成图表失败');
  } finally {
    formLoading.value = false;
  }
};

const handleReset = () => {
  form.value = {
    goal: '',
    name: '',
    chartType: '',
    dataId: '',
    selectedColumns: []
  };
  availableColumns.value = [];
};

onMounted(async () => {
  await handleReset();
  let res = await getMyDataApi();
  if (res && res.code == 200) {
    options.value = res.data;
  }
});
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

/* 多选标签样式调整 */
:deep(.el-select__tags) {
  flex-wrap: nowrap;
  overflow-x: auto;
  max-width: 100%;
}

:deep(.el-tag) {
  margin-right: 6px;
  margin-bottom: 6px;
}
</style>