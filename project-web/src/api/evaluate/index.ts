import http from "@/http";
import {Evaluate} from "./evaluateModel";
export const addEvaluate=(param:Evaluate)=>{
    return http.post('/api/evaluate/add',param)
}
export const getEvaluateListApi=(param:Object)=>{
    return http.get("/api/evaluate/list",param)
}
export const getScoreRateApi=(param:Object)=>{
    return http.get("/api/evaluate/evaluateList",param)
}


export const getChartApi=(param:Object)=>{
    return http.get("/api/evaluate/statistic",param)
}


   
   





   

