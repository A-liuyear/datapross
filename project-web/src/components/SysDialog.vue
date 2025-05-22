<template>
        <el-dialog
            :model-value="props.visible"
            :title="props.title"
            :width="props.width+'px'"
            :before-close="onClose"
            append-to-body
            :close-on-click-modal="false"
        >
        <!--展示内容-->
            <div class="container" :style="{height:props.height+'px'}">
                <slot name="content"></slot>
            </div>
            <template #footer>
            <div class="dialog-footer">
                <el-button type="danger" @click="onClose">取消</el-button>
                <el-button type="primary" @click="onConfirm">确定</el-button>
            </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
//定义参数类型
interface DialogProps{
    title?:string,
    visible?:boolean,
    width?:number,
    height?:number
}
//接收父组件传递的数据 使用有默认值的方式withDefaults
const props=withDefaults(defineProps<DialogProps>(),{
    title:'标题',
    visible:false,
    width:630,
    height:280
})
//注册事件
const emit=defineEmits(["onClose","onConfirm"])
//关闭弹窗
const onClose=()=>{
    emit('onClose')
}
const onConfirm=()=>{
    emit('onConfirm')
}
</script>

<style scoped lang="scss">
.container{
    overflow-x: initial;
    overflow-y: auto;
}
.el-dialog{
 border-top-left-radius: 7px !important;
 border-top-right-radius: 7px !important;
 .el-dialog__header{
    background-color: #009688 !important;
    margin-right: 0px;
    border-top-left-radius: 7px !important;
    border-top-right-radius: 7px !important;
    .el-dialog__title{
        color: #fff;
        font-size: 16px;
        font-weight: 600;
    }
 }

.el-dialog__headerbtn{
    .el-dialog__close{
        color: #fff;
    }
}
.el-dialog__body{
    padding: 10px;
}
.el-dialog__footer{
    border-top: 1px solid #e8eaec !important;
    padding: 10px;
}
}
</style>