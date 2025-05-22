import http from '@/http'

export const getMonitorLineApi=()=>{
    return http.get("/api/echarts/monitor")
}
export const getMyChartApi=(obj:Object)=>{
    return http.get("/api/chart/list",obj)
}


export const getMyImagesApi=(obj:Object)=>{
    return http.get("/api/chart/imageAiList",obj)
}

