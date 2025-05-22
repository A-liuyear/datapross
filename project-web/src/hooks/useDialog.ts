import { reactive } from "vue";
import {DialogModel} from '@/type/BaseType'
export default function useDialog(){

    const dialog=reactive<DialogModel>({
       title:'',
       width:630,
       height:280,
       visible:false
    })
    const onShow=()=>{
        dialog.visible=true;
    }

    const onClose=()=>{
        dialog.visible=false;
    }

    const onConfirm=()=>{
        dialog.visible=false;
    }
    return {
        dialog,
        onShow,
        onClose,
        onConfirm
    }
}