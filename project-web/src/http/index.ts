import axios ,{AxiosInstance,InternalAxiosRequestConfig,AxiosResponse,AxiosRequestConfig}from "axios";
import   {ElMessage}  from 'element-plus'
import { useUserStore } from "@/store/user";
const config={
    baseURL:'http://localhost:8089',
    // baseURL:'/api',
    timeout:100000,
    withCrendentials:true
}
//定义返回值类型
export interface Result<T =any>{
    code: number,
    msg:string,
    data:T
}

// 定义Excel数据返回类型
export interface ExcelSheetData {
    name: string;       // 工作表名称
    headers: string[];  // 表头数组
    data: Record<string, any>[]; // 数据行（每行是一个对象，key是表头，value是单元格值）
}

class Http{
    //axios的实例
    private instance: AxiosInstance;
    //初始化
    constructor(configs:AxiosRequestConfig){
        //创建axios的实例
        this.instance=axios.create(configs)
        //配置拦截器
        this.interceptors()

    }
    private interceptors(){
        //请求发送之前的处理:请求头携带token
        this.instance.interceptors.request.use((config:InternalAxiosRequestConfig)=>{
        let userStore=useUserStore()
        let token=userStore.getToken;
        if(token&&config){
            config.headers['token']=token;//自定义token参数
        }
        return config;
        }),(error:any)=>{
            error.data={}
            error.data.msg='服务器异常请联系管理员'
            return error;
        }
        //请求返回之后的处理
        this.instance.interceptors.response.use((res:AxiosResponse)=>{
            let userStore=useUserStore()
            //token过期返回600
            if(res.data.code==600){
             userStore.setToken('')
             userStore.setUserId('')
             localStorage.clear()
             window.location.href='/login'
            }else{
                return res.data
            }
         }),(error:any)=>{
            console.log('进入错误')
            error.data={}
            if(error&&error.response){
                switch(error.response.status){
                    case 400:
                        error.data.msg='错误请求';
                        ElMessage.error(error.data.msg)
                        break
                    case 401:
                        error.data.msg='未授权，请重新登录';
                        ElMessage.error(error.data.msg)
                        break
                    case 403:
                        error.data.msg='拒绝访问';
                        ElMessage.error(error.data.msg)
                        break
                    case 404:
                        error.data.msg='未找到接口';
                        ElMessage.error(error.data.msg)
                        break
                    }

            }else{
                error.data.msg='连接服务器异常';
                ElMessage.error(error.data.msg)
            }
            return Promise.reject(error)
        }
    }
    //post请求
    post<T =Result>(url:string,data?:object):Promise<T>{
        return this.instance.post(url,data);
    }
    //put请求
    put<T =Result>(url:string,data?:object):Promise<T>{
        return this.instance.put(url,data);
    }
    //get请求
    get<T =Result>(url:string,params?:object):Promise<T>{
        return this.instance.get(url,{params});
    }
    //delete请求
    delete<T =Result>(url:string):Promise<T>{
        return this.instance.delete(url);
    }
    async downloadExcel(
        url: string,
        params?: object,
        filename?: string
    ): Promise<void> {
        try {
            const response:any = await this.instance.get(url, {
                params,
                responseType: 'blob' // 重要：指定响应类型为二进制流
            });
            console.log(response)
            // 从响应头获取文件名（如果后端设置了Content-Disposition）
            // const contentDisposition = response.headers['content-disposition'];
            const defaultName = filename || 'download.xlsx';
            const downloadFilename = defaultName ;

            // 创建下载链接
            const blob = new Blob([response], {
                type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            });
            const downloadUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = downloadUrl;
            link.download = downloadFilename;
            document.body.appendChild(link);
            link.click();
            
            // 清理
            window.URL.revokeObjectURL(downloadUrl);
            document.body.removeChild(link);
            
            return Promise.resolve();
        } catch (error) {
            console.error('下载Excel失败:', error);
            ElMessage.error('文件下载失败');
            return Promise.reject(error);
        }
    }
     /**
     * 获取Excel文件并解析为JSON数据
     * @param url 请求地址
     * @param params 请求参数
     * @returns 解析后的Excel数据数组
     */
     async getExcel<T = ArrayBuffer>(
        url: string, 
        params?:  Record<string, any> 
    ): Promise<T> {
        try {
            // 请求Excel文件流，responseType必须设置为arraybuffer
            const response:any = await this.instance.get(url, {
                params,
                responseType: 'arraybuffer'
            });
            // 解析Excel数据
            return response;
        } catch (error) {
            console.error('获取或解析Excel失败:', error);
            ElMessage.error('获取Excel文件失败');
            return Promise.reject(error);
        }
    }

    //图片上传
    upload<T=Result>(url:string,params?:object):Promise<T>{
        return this.instance.post(url,params,{
            headers:{
                'Content-Type':'multipart/form-data'
            }
        });
    }
}

export default new Http(config)