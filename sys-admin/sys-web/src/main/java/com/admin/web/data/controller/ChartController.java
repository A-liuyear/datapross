package com.admin.web.data.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.admin.common.util.Constant;
import com.admin.common.util.ResultUtils;
import com.admin.common.util.ResultVo;
import com.admin.config.exception.BusinessException;
import com.admin.config.rabbitmq.RabbitMQConfig;
import com.admin.config.redis.RedisUtils;
import com.admin.web.data.entity.*;
import com.admin.web.data.entity.ai.BIResponse;
import com.admin.web.data.service.ChartService;
import com.admin.web.data.service.ImageService;
import com.admin.web.user.entity.SysUser;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.Map;

/**
 * 图表接口
 */
@RestController
@RequestMapping("/api/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ImageService imageService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public ResultVo<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(500,"参数错误");
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser= JSON.parseObject(user,SysUser.class);
        chart.setUserId(sysUser.getUserId());
        boolean result = chartService.save(chart);
        long newPostId = chart.getId();
        return ResultUtils.success();
    }



    /**
     * 根据 AI 同步生成图表（同步）
     * @param genChartByAIRequest
     * @return
     */
    @PostMapping("/generateChart")
    public ResultVo<BIResponse> genChartByAI(@RequestBody
                                             GenChartByAIRequest genChartByAIRequest) throws Exception {
        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser= JSON.parseObject(user,SysUser.class);
        Long userId=sysUser.getUserId();
        genChartByAIRequest.setUserId(userId);
        BIResponse biResponse = chartService.genChartByAI(genChartByAIRequest);
        return ResultUtils.success("生成成功",biResponse);
    }



    /**
     * 根据 AI 同步生成图表（异步）
     * @param genChartByAIRequest
     * @return
     */
    @PostMapping("/generateChartAsync")
    public ResultVo<BIResponse> generateChartAsync(@RequestBody
                                             GenChartByAIRequest genChartByAIRequest) throws Exception {
        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser= JSON.parseObject(user,SysUser.class);
        Long userId=sysUser.getUserId();
        genChartByAIRequest.setUserId(userId);
        sendMessage(JSON.toJSONString(genChartByAIRequest));
        return ResultUtils.success("发送成功");
    }

    //图片理解  同步
    @PostMapping("/imageAnalysis")
    public ResultVo<ImageAnalysisResponse> analyzeUploadedImage( @RequestBody Map<String,String> map) throws Exception {
        ImageAnalysisRequest request = new ImageAnalysisRequest();
        request.setImageUrl(map.get("fileUrl"));
        request.setObjectDetection(true);
        request.setSceneUnderstanding(true);
        request.setGenerateDescription(true);
        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser= JSON.parseObject(user,SysUser.class);
        Long userId=sysUser.getUserId();
        request.setUserId(userId);
        ImageAnalysisResponse imageAnalysisResponse = chartService.analyzeImage(request);
        return ResultUtils.success("生成成功",imageAnalysisResponse);

    }
    //图片理解  异步
    @PostMapping("/imageAnalysisAsync")
    public ResultVo<ImageAnalysisResponse> imageAnalysisAsync( @RequestBody Map<String,String> map) throws Exception {
        ImageAnalysisRequest request = new ImageAnalysisRequest();
        request.setImageUrl(map.get("fileUrl"));
        request.setObjectDetection(true);
        request.setSceneUnderstanding(true);
        request.setGenerateDescription(true);
        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser= JSON.parseObject(user,SysUser.class);
        Long userId=sysUser.getUserId();
        request.setUserId(userId);
        sendMessageImg(JSON.toJSONString(request));
        return ResultUtils.success("发送成功");

    }
    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public ResultVo<Chart> getChartById(long id, HttpServletRequest request) {

        Chart chart = chartService.getById(id);

        return ResultUtils.success("查询成功",chart);
    }


    @GetMapping("/list")
    public ResultVo list(OrderParam order){
        IPage<Chart> page=new Page<>(order.getCurrentPage(),order.getPageSize());
        QueryWrapper<Chart> queryWrapper=new QueryWrapper<>();


        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser=JSON.parseObject(user,SysUser.class);
        if(!"1".equals(sysUser.getIsAdmin())){
            queryWrapper.lambda().eq(Chart::getUserId,sysUser.getUserId());
        }
        queryWrapper.lambda().orderByDesc(Chart::getUpdateTime);
        IPage<Chart> list = chartService.page(page, queryWrapper);

        return ResultUtils.success("查询成功",list);
    }

    @GetMapping("/imageAiList")
    public ResultVo imageAiList(ImageParam param){
        IPage<ImageAi> page=new Page<>(param.getCurrentPage(),param.getPageSize());
        QueryWrapper<ImageAi> queryWrapper=new QueryWrapper<>();


        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser=JSON.parseObject(user,SysUser.class);
        if(!"1".equals(sysUser.getIsAdmin())){
            queryWrapper.lambda().eq(ImageAi::getUserId,sysUser.getUserId());
        }
        queryWrapper.lambda().orderByDesc(ImageAi::getCreateTime);
        IPage<ImageAi> list = imageService.page(page, queryWrapper);

        return ResultUtils.success("查询成功",list);
    }




    public static String compressAndEncodeToBase64(File file, int targetWidth, int targetHeight, float quality) throws Exception {
        BufferedImage originalImage = ImageIO.read(file);

        // 调整图片大小（保持宽高比）
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(
                originalImage.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH),
                0, 0, null
        );

        // 压缩为 JPEG 并控制质量
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        baos.flush();

        // 转为 Base64
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
    /**
     * 发送消息
     * @param message 消息内容
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE_NAME_DATA,
                RabbitMQConfig.ROUTING_KEY_DATA,
                message
        );
        System.out.println("消息发送成功: " + message);
    }

    /**
     * 发送消息
     * @param message 消息内容
     */
    public void sendMessageImg(String message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE_NAME_IMG,
                RabbitMQConfig.ROUTING_KEY_IMG,
                message
        );
        System.out.println("消息发送成功: " + message);
    }

}
