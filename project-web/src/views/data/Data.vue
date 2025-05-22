<template>
    <el-main height="">
        <el-form :model="searchParam"  :inline="true" size="default">
            <el-form-item >
                <el-input placeholder="请输入文件名称" v-model="searchParam.fileName"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button  icon="Search"  @click="searchBtn">搜索</el-button>
                <el-button  icon="Close" plain type="danger" @click="resetBtn">重置</el-button>
                <el-button v-if="global.$hasPerm(['sys:data:add'])"  icon="Plus" type="primary" @click="addBtn">发布</el-button>
            </el-form-item>
        </el-form>

         <!--表格-->
         <el-table :height="tableHeight" :data="tableList" border stripe >
            <el-table-column type="index" width="50" label="序号" />
            <el-table-column prop="fileName" label="文件名称"></el-table-column>
            <el-table-column prop="fileType" label="文件类型"> </el-table-column>
            <el-table-column prop="price" label="价格"></el-table-column>
            <el-table-column prop="remark" label="描述"></el-table-column>
            <el-table-column label="上/下架" align="center" key="status" >
                <template #default="scope">
                  <el-switch
                    v-model="scope.row.status"
                    :active-value="'1'"
                    :inactive-value="'0'"
                    @change="handleStatusChange(scope.row)"
                  ></el-switch>
                </template>
              </el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="updateTime" label="更新时间"></el-table-column>
            <el-table-column  label="操作" width="350" align="center" v-if="global.$hasPerm(['sys:data:edit','sys:data:delete'])">
                <template #default="scope">
                    <el-button v-if="global.$hasPerm(['sys:data:edit'])"  type="primary" icon="Edit" size="default" @click="editBtn(scope.row)">
                     编辑
                    </el-button>
                    <el-button   type="warning" icon="Delete" size="default" @click="openPreviewDialog(scope.row.dataId)">预览excel</el-button>
                    <el-button  v-if="global.$hasPerm(['sys:data:delete'])" type="danger" icon="Delete" size="default" @click="delBtn(scope.row.dataId)">删除</el-button>
                 
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

<!-- 预览弹窗 -->
<el-dialog
      v-model="dialogVisible"
      title="Excel预览"
      width="80%"
      top="5vh"
      destroy-on-close
    >
      <div v-loading="loading" class="excel-preview-container">
        <div v-if="errorMsg" class="error-message">
          <el-alert :title="errorMsg" type="error" show-icon />
        </div>

        <div v-if="sheets.length > 0" class="excel-content">
          <el-tabs v-model="activeSheet" type="card">
            <el-tab-pane
              v-for="sheet in sheets"
              :key="sheet.name"
              :label="sheet.name"
              :name="sheet.name"
            >
              <div class="table-container">
                <el-table
                  :data="sheet.data"
                  border
                  height="60vh"
                  style="width: 100%"
                  highlight-current-row
                >
                  <el-table-column
                    v-for="(header, index) in sheet.headers"
                    :key="index"
                    :prop="header"
                    :label="header"
                    min-width="120"
                  />
                </el-table>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-dialog>



        <SysDialog
        :title="dialog.title"
        :width="dialog.width"
        :visible="dialog.visible"
        :height="dialog.height"
        @on-close="onClose"
        @on-confirm="onCommit"
        >
        <template v-slot:content>
            <el-form
                :model="addModel"
                ref="addForm"
                :rules="rules"
                label-width="80px"
                :inline="false"
                size="default">
            <el-row >
                <el-col :span="12" :offset="0">
                        <el-form-item  prop="fileName" label="文件名称">
                            <el-input v-model="addModel.fileName"></el-input>
                        </el-form-item>
                </el-col>
                <el-col :span="12" :offset="0">
                        <el-form-item prop="price" label="价格">
                            <el-input-number v-model="addModel.price" :min="1"  />
                        </el-form-item>
                </el-col>
            </el-row>
            <el-row >
               
                <el-col :span="12" :offset="0">
                        <el-form-item prop="dataTotal" label="数据量">
                            <el-input-number v-model="addModel.dataTotal"  />
                        </el-form-item>
                </el-col>
            </el-row>
           <!-- 新增分类树选择 -->
        <el-row>
          <el-col :span="24" :offset="0">
            <el-form-item prop="category" label="数据分类">
              <el-tree
                :data="categoryTree"
                node-key="label"
                :props="treeProps"
                :highlight-current="true"
                @node-click="handleNodeClick"
                style="margin-top: 10px;"
              ></el-tree>
            </el-form-item>
          </el-col>
        </el-row>
            <el-row >
                <el-col :span="24" :offset="0">
                        <el-form-item prop="remark"  label="文件描述">
                             <el-input
                                v-model="addModel.remark"
                                style="width: 450px;margin-top: 10px;"
                                :autosize="{ minRows: 2, maxRows: 6 }"
                                type="textarea"
                                placeholder="请输入描述信息"
                            />
                        </el-form-item>
                </el-col>
            </el-row>
            <el-row >
                <el-col :span="12" :offset="0">
                <el-upload
                    v-model:file-list="fileList"
                    class="upload-demo"
                    style="margin-left: 80px;"
                    :on-preview="handlePreview"
                    :on-remove="handleRemove"
                    :limit="1"
                    :on-exceed="handleExceed"
                    :before-upload="beforeUpload"
                    :http-request="customUpload" 
            >
                        <el-button type="primary">上传</el-button>
                        <template #tip>
                            <div class="el-upload__tip">
                              仅支持上传 1M 以内的 Excel 文件
                            </div>
                        </template>
                    </el-upload>
                </el-col>
            </el-row>
        </el-form>
        </template>
        </SysDialog>
    </el-main>
    
</template>

<script setup lang="ts">
import { onMounted, reactive,ref ,nextTick} from 'vue';
import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { ElMessage, FormInstance } from 'element-plus';
import {addData,getList,delData,editData,updateStatus,uploadApi,getExcelApi} from '@/api/data/index'
import {DataFile} from '@/api/data/dataModel'
import useInstance from '@/hooks/useInstance'
import type { UploadProps, UploadUserFile } from 'element-plus'
import * as XLSX from 'xlsx'
// 弹窗状态
const dialogVisible = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const sheets:any = ref([])
const activeSheet = ref('')
//获取全局global
const {global}=useInstance()
const {dialog,onClose,onShow}=useDialog()
const addForm=ref<FormInstance>()
   
const searchParam=reactive({
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
    pageSize:10,
    total:0
})
const addModel=reactive({
    dataId:'',
    fileName:'',
    price: 0,
    remark:'',
    url:'',
    category:'',
    status:'1',
    dataTotal:0
})
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
//表格高度
const tableHeight=ref(0)

const tags=ref('')
// 树组件配置
const treeProps = {
  children: 'children',
  label: 'label'
}
const handleStatusChange=(row:any) =>{
  let text = row.status === '0' ? "下架" : "上架";
  const confirm= global.$confirm('确认要' + text + '"' + row.fileName + '"吗?')
  if(confirm){
    let res:any= updateStatus({dataId:row.dataId, status:row.status});
    if(res&&res.code==200){
      ElMessage.success(res.msg)
    }
  }
};

// 处理节点点击
const handleNodeClick = (data:any) => {
  // 只允许选择叶子节点（没有children的节点）
  if (!data.children || data.children.length === 0) {
    addModel.category= data.label
  }
}
let options=ref([])

const rules=reactive({
    fileName:[
        {
            required:true,
            message:'请填写文件名称',
            trigger:["change","blur"]
        }
    ],
    price:[
        {
            required:true,
            message:'请填写价格',
            trigger:["change","blur"]
        }
    ],
    remark:[
        {
            required:true,
            message:'请填写描述',
            trigger:["change","blur"]
        }
    ],
    category:[
        {
            required:true,
            message:'请选择分类',
            trigger:["change","blur"]
        }
    ],
})
// 打开预览弹窗
const openPreviewDialog = async (dataId:number) => {
  dialogVisible.value = true
  await nextTick
  await fetchExcelData(dataId)
}

// 从后端获取Excel数据
const fetchExcelData = async (dataId:number) => {
  loading.value = true
  errorMsg.value = ''
  sheets.value = []
   console.log(dataId)
  let data=await getExcelApi(dataId)
  parseExcelData(data)
  loading.value = false
}

// 解析Excel数据
const parseExcelData = (arrayBuffer:any) => {
  try {
    const data = new Uint8Array(arrayBuffer)
    const workbook = XLSX.read(data, { type: 'array' })
    
    const sheetNames = workbook.SheetNames
    const parsedSheets = sheetNames.map(sheetName => {
      const worksheet = workbook.Sheets[sheetName]
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })
      
      // 提取表头（假设第一行是表头）
      const headers:any = jsonData[0] || []
      const rows = jsonData.slice(1)
     
      // 转换为对象数组格式
      const data = rows.map((row:any) => {
        const obj:any = {}
        headers.forEach((header:any, index:number) => {
          obj[header] = row[index]
        })
        return obj
      })
      
      return {
        name: sheetName,
        headers,
        data
      }
    })
    
    sheets.value = parsedSheets
    if (parsedSheets.length > 0) {
      activeSheet.value = parsedSheets[0].name
    }
  } catch (error) {
    console.error('Excel解析错误:', error)
    errorMsg.value = 'Excel文件解析失败，请检查文件格式'
  }
}
const addBtn=()=>{
    dialog.title='新增';
    dialog.height=450
    tags.value='0'
    onShow()
    options.value=[]
    fileList.value = []
    addModel.dataId='';
    //清空表单
    addForm.value?.resetFields()
}

const fileList = ref<UploadUserFile[]>([])

const handleRemove: UploadProps['onRemove'] = (_file, _uploadFiles) => {
    addModel.url=''
    addModel.fileName=''
    fileList.value = []

}

const handlePreview: UploadProps['onPreview'] = (_uploadFile) => {
}

const handleExceed: UploadProps['onExceed'] = async(_files, _uploadFiles) => {
  ElMessage.warning(
    `限制只能上传一个文件`
  )
  
  
}
const beforeUpload = (file:any) => {
  const isExcel = file.type === 'application/vnd.ms-excel' || file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
  if (!isExcel) {
    alert('只能上传 Excel 文件');
    addModel.url=''
    addModel.fileName=''
    fileList.value = []
    return false;
  }
  const isLt1M = file.size / 1024 / 1024 < 1;
  if (!isLt1M) {
    alert('文件大小不能超过 1M');
    addModel.url=''
    addModel.fileName=''
    fileList.value = []
    return false;
  }
  return true;
};

// 自定义上传函数
const customUpload = async (options:any) => {
  const { file } = options;
    // 调用你封装的Http类的upload方法
  const formData = new FormData();
  formData.append('file', file);
  let res=await uploadApi(formData)
  if(res&&res.code==200){
    addModel.url=res.data;
    addModel.fileName=file.name;
    ElMessage.success(res.msg) 
    // fileList.value.push({
    //   name: file.name,
    //   status: 'success'
    // })
  }
}



//编辑按钮
const editBtn=async (row:DataFile)=>{
    dialog.title="编辑"
    dialog.height=450
    options.value=[]
    onShow()
    nextTick(()=>{
        Object.assign(addModel,row)
        //设置角色id
    })
    
     //清空表单
     addForm.value?.resetFields()
   
}

const sizeChange=(size:number)=>{
    searchParam.pageSize=size;
    list()
}

const currentChange=(page:number)=>{
    searchParam.currentPage=page;
    list()
}


//删除按钮
const delBtn=async (dataId:string)=>{
    const confirm=await global.$confirmFunc("确定删除该数据吗?");//返回boolean
    if(confirm){
        let res=await delData(dataId)
        if(res&&res.code==200){
           ElMessage.success(res.msg) 
           list()
    }
}
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

//表单提交
const onCommit=()=>{
    addForm.value?.validate(async (valid)=>{
        if(valid){
            console.log("表单验证通过")
            //提交请求
            let res:any=null
            if(addModel.dataId){
                res=await editData(addModel)
            }else{
                res=await addData(addModel)
            }
            if(res&&res.code==200){
                ElMessage.success(res.msg)
                onClose()
                list()
        }else{
            ElMessage.error(res.msg)
        }
          
    }
})
}

const resetBtn=()=>{
    searchParam.fileName=''
    searchParam.currentPage=1
    list()
}




onMounted(()=>{
    nextTick(()=>{
        tableHeight.value=window.innerHeight-230
    })
    list()
})
</script>

<style scoped>
.excel-preview-container {
  padding: 10px;
}

.error-message {
  margin-bottom: 20px;
}

.table-container {
  margin-top: 15px;
}

.el-tabs {
  margin-top: 10px;
}
</style>