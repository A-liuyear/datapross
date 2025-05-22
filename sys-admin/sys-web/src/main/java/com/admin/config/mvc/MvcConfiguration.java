package com.admin.config.mvc;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.admin.common.util.ResultUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
    @Value("web.loadPath")
    private String loadPath;


    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter ()
                .addInclude("/**")
                .addExclude("/api/video/downloadFile")
                .addExclude("/api/video/viewFile")
                .addExclude("/api/user/register")
                .addExclude("/signaling")
                .addExclude("/api/user/login")
                .addExclude("/api/user/getImage")
                .addExclude("/api/upload/uploadImage")
                .addExclude("/images/**")
                .addExclude("/api/data/uploadFile")



                //认证函数 : 每次请求都会进行拦截
                .setAuth(obj->{
                    // 输出 API 请求日志，方便调试代码
                   SaManager.getLog().debug("----- 请求path={}  提交token={}", SaHolder.getRequest().getRequestPath(), StpUtil.getTokenValue());
                    // 检查是否登录
                    SaRouter.match("/**","/api/user/login").check(r -> StpUtil.checkLogin());
                })
            //  异常处理函数：每次认证函数发生异常时执行此函数
               .setError(e -> {
                   // 设置响应头
                   SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                   if (e instanceof NotLoginException) {
                       return SaResult.error(e.getMessage()).setCode(600);
                  }
                   return ResultUtils.error(e.getMessage());
               })
                // 前置函数：在每次认证函数之前执行（BeforeAuth 不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(r->{
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(re-> System.out.println("------OPTIONS预检查请求，不做处理"))
                            .back();
                });

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(loadPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }
}
