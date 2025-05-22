import http from "@/http";
import { OrderParam} from "./orderModel";

export const getList=(param:OrderParam)=>{
 return http.get("/api/order/list",param)
}


export const downLoadExcelApi=(obj:Object,dataName:string)=>{
    return http.downloadExcel("/api/data/excel",{id:obj},dataName)
}


export const exportExcel=()=>{
    return http.downloadExcel("/api/order/exportExcel",{},"交易记录")
}

   


   

