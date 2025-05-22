package com.admin.config.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.admin.common.util.ResultUtils;
import com.admin.common.util.ResultVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理SaToken异常
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public ResultVo handlerNotLoginException(BusinessException ex) {
        // 返回给前端
        return ResultUtils.error(ex.getMessage(),ex.getCode());
    }



    /**
      * 处理SaToken异常
      */
    @ResponseBody
    @ExceptionHandler(value = NotLoginException.class)
    public ResultVo handlerNotLoginException(NotLoginException nle) {
        // 判断场景值，定制化异常信息
        String message;
        if (nle.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未提供token";
        } else if (nle.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "token无效";
        } else if (nle.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "token已过期";
        } else {
            message = "当前会话未登录";
        }
        // 返回给前端
        return ResultUtils.error(message,500);
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResultVo handlerException(Exception exception) {
        // 返回给前端
        return ResultUtils.error(exception.getMessage(),500);
    }
 }
