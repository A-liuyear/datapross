<template>
    <div class="chart-container">
      <VChart
        v-if="option"
        :option="mergedOption"
        :autoresize="true"
        :theme="theme"
        :title="title"
        :init-options="initOptions"
        style="width: 100%; height: 100%"
      />
      <div v-else-if="loading" class="loading-state">
        <span class="loading-text">图表加载中...</span>
      </div>
      <div v-else class="error-state">
        <span class="error-text">图表数据加载失败</span>
        <button @click="fetchData">重试</button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, watch, onMounted } from 'vue';
  import { use } from 'echarts/core';
  import { CanvasRenderer } from 'echarts/renderers';
  import {
    BarChart,
    PieChart,
    LineChart,
    RadarChart,
    ScatterChart,
  } from 'echarts/charts';
  import {
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent,
    PolarComponent,
    DatasetComponent,
    DataZoomComponent,
  } from 'echarts/components';
  import VChart from 'vue-echarts';
  
  // 注册必要的组件
  use([
    CanvasRenderer,
    BarChart,
    PieChart,
    LineChart,
    RadarChart,
    ScatterChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent,
    PolarComponent,
    DatasetComponent,
    DataZoomComponent,
  ]);
  
  const props = defineProps({
    // 图表配置数据
    chartData: {
      type: Object,
      default: () => ({}),
    },
    // 主题
    theme: {
      type: String,
      default: 'light',
    },
    // 是否自动请求数据
    autoFetch: {
      type: Boolean,
      default: false,
    },
    // API地址（如果autoFetch为true）
    apiUrl: {
      type: String,
      default: '',
    },
    title: {
      type: String,
      default: '',
    },
    data: {
      type: String,
      default: '',
    },
    // 额外的配置项
    extraOptions: {
      type: Object,
      default: () => ({}),
    },
    // 初始化配置
    initOptions: {
      type: Object,
      default: () => ({
        renderer: 'canvas',
      }),
    },
  });
  
  const emit = defineEmits(['chart-click', 'data-loaded', 'data-error']);
  
  const option = ref(null);
  const loading = ref(false);
  const error = ref(null);
  
  // 合并后的配置
  const mergedOption = computed(() => {
    return {
      ...props.extraOptions,
      ...option.value,
    };
  });
  
  // 获取数据
  const fetchData = async () => {
    if (!props.autoFetch || !props.apiUrl) return;
    
    loading.value = true;
    error.value = null;
    
    try {
      const response = await fetch(props.apiUrl);
      const data = await response.json();
      option.value = normalizeOption(data);
      emit('data-loaded', data);
    } catch (err) {
      error.value = err;
      emit('data-error', err);
    } finally {
      loading.value = false;
    }
  };
  
  // 标准化配置
  const normalizeOption = (rawOption) => {
    // 如果是字符串，先解析为对象
  if (typeof rawOption === 'string') {
    try {
      rawOption = JSON.parse(rawOption);
    } catch (e) {
      console.error('JSON 解析失败:', e);
      return { title: { text: '数据格式错误' } };
    }
  }
    // 确保有基本的title配置
    if (!rawOption.title) {
      rawOption.title = {
        text: '',
        left: 'center',
        textStyle: {
          fontSize: 16,
        },
      };
    }
    
    // 确保有基本的tooltip配置
    if (!rawOption.tooltip) {
      rawOption.tooltip = {
        trigger: 'item',
      };
    }
    
    return rawOption;
  };
  
  // 图表点击事件
  const handleChartClick = (params) => {
    emit('chart-click', params);
  };
  
  // 监听props变化
  watch(
    () => props.chartData,
    (newVal) => {
      if (newVal) {
        option.value = normalizeOption(newVal);
      }
    },
    { immediate: true, deep: true }
  );
  
  // 监听apiUrl变化
  watch(
    () => props.apiUrl,
    () => {
      if (props.autoFetch) {
        fetchData();
      }
    }
  );
  
  onMounted(() => {
    if (props.autoFetch) {
      fetchData();
    }
  });
  </script>
  
  <style scoped>
  .chart-container {
    position: relative;
    width: 100%;
    height: 100%;
    min-height: 400px;
  }
  
  .loading-state,
  .error-state {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    color: #666;
  }
  
  .error-state button {
    margin-left: 10px;
    padding: 4px 8px;
    background: #f0f0f0;
    border: 1px solid #ccc;
    border-radius: 3px;
    cursor: pointer;
  }
  
  .error-state button:hover {
    background: #e0e0e0;
  }
  </style>