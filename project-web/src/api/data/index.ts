import http from "@/http";
import { DataListParam, DataFile, Order,DataFileParam} from "./dataModel";
export const addData=(param:DataFile)=>{
    return http.post('/api/data',param)
}

export const getList=(param:DataListParam)=>{
 return http.get("/api/data/list",param)
}

export const editData=(param:DataFile)=>{
    return http.put('/api/data',param)
}

export const updateStatus=(param:DataFileParam)=>{
    return http.put('/api/data',param)
}

export const uploadImg=(obj:Object)=>{
    return http.post("/api/chart/imageAnalysis",obj)
}

export const uploadImgAsync=(obj:Object)=>{
    return http.post("/api/chart/imageAnalysisAsync",obj)
}



export const delData=(dataId:string)=>{
    return http.delete(`/api/data/${dataId}`)
}
export const uploadApi=(obj:Object)=>{
    return http.upload("/api/data/uploadFile",obj)
}

export const getExcelApi=(obj:Object)=>{
    return http.getExcel("/api/data/excel",{id:obj})
}


  export const getDataColumns = (dataId: string) => {
    return http.get(`/api/data/columns/${dataId}`);
  };



export const getById=(id:Object)=>{
    return http.get(`/api/data/${id}`)
   }
export const payDataApi=(param:Order)=>{
    return http.post('/api/data/payData',param)
}

export const getMyDataApi=()=>{
    return http.post("/api/data/getMyData")
}

export const generateChart=(obj:Object)=>{
    return http.post("/api/chart/generateChart",obj)
}

export const generateChartAsync=(obj:Object)=>{
    return http.post("/api/chart/generateChartAsync",obj)
}
   


   

