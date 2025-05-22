package com.admin.common.util;

public class ResultUtils {
    public static ResultVo success() {
        return Vo(null, Constant.SUCCESS,null);
    }

    public static ResultVo success(String msg) {
        return Vo(msg,Constant.SUCCESS,null);
    }

    public static ResultVo success(String msg,Object data) {
        return Vo(msg,Constant.SUCCESS,data);
    }



    public static ResultVo ERROR() {
        return Vo(null,Constant.ERROR_CODE,null);
    }

    public static ResultVo error(String msg) {
        return Vo(msg,Constant.ERROR_CODE,null);
    }
    public static ResultVo error(String msg,int code) {
        return Vo(msg,code,null);
    }
    public static ResultVo error(String msg,int code,Object data) {
        return Vo(msg,code,data);
    }

    public static ResultVo Vo(String msg,int code,Object data) {
        return new ResultVo(msg,code,data);
    }

}
