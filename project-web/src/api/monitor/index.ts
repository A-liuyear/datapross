import http from "@/http";
import { MointorListParam} from "./monitor";

export const getList=(param:MointorListParam)=>{
 return http.get("/api/monitor/list",param)
}



