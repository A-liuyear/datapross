package com.admin.web.data.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.admin.common.util.Constant;
import com.admin.common.util.InputStreamToResourceConverter;
import com.admin.common.util.ResultUtils;
import com.admin.common.util.ResultVo;
import com.admin.config.minio.MinioConfig;
import com.admin.config.minio.MinioUtils;
import com.admin.config.redis.RedisUtils;
import com.admin.web.data.entity.DataFile;
import com.admin.web.data.entity.Order;
import com.admin.web.data.entity.OrderParam;
import com.admin.web.data.service.DataFileService;
import com.admin.web.data.service.OrderService;
import com.admin.web.user.entity.SysUser;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@RequestMapping("/api/order")
@RestController
public class OrderController {
    @Autowired
    private DataFileService dataFileService;
    @Autowired
    private OrderService orderService;


    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private RedisUtils redisUtils;



    @GetMapping("/list")
    public ResultVo list(OrderParam order){
        IPage<Order> page=new Page<>(order.getCurrentPage(),order.getPageSize());
        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotEmpty(order.getDataName())){
            queryWrapper.lambda().like(Order::getDataName,order.getDataName());
        }

        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser=JSON.parseObject(user,SysUser.class);
        if(!"1".equals(sysUser.getIsAdmin())){
            queryWrapper.lambda().eq(Order::getUserId,sysUser.getUserId());
        }
        queryWrapper.lambda().orderByDesc(Order::getUpdateTime);
        IPage<Order> list = orderService.page(page, queryWrapper);

        return ResultUtils.success("查询成功",list);
    }

    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportExcel( Long id ) throws Exception {
        DataFile byId = dataFileService.getById(id);
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(byId.getUrl())
                        .build())) {

            // 2. 设置响应头
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + byId.getFileName())
                    .body(InputStreamToResourceConverter.convert(stream));
        }
    }

    @GetMapping("/exportExcel")
    public ResponseEntity<byte[]> exportWithEasyExcel() throws Exception {
        List<Order> list = orderService.list();
        // 2. 使用 EasyExcel 写入内存
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, Order.class)
                .sheet("用户数据")
                .doWrite(list);

        // 3. 设置 HTTP 响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "trade.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }
}
