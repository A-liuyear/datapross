package com.admin.web.data.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.admin.common.util.*;
import com.admin.config.ai.CommonConstant;
import com.admin.config.ai.ImageCaptionManager;
import com.admin.config.ai.ZhiPuAIManager;
import com.admin.config.email.EmailUtil;
import com.admin.config.exception.BusinessException;
import com.admin.config.minio.MinioConfig;
import com.admin.config.minio.MinioUtils;
import com.admin.config.redis.RedisUtils;
import com.admin.web.data.entity.*;
import com.admin.web.data.entity.ai.BIResponse;
import com.admin.web.data.mapper.ChartMapper;
import com.admin.web.data.service.ChartService;
import com.admin.web.data.service.DataFileService;
import com.admin.web.data.service.ImageService;
import com.admin.web.data.service.MsgLogService;
import com.admin.web.user.entity.SysUser;
import com.admin.web.user.service.SysUserService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hejiajun
 * @description 针对表【chart(图表信息表)】的数据库操作Service实现
 * @createDate 2024-01-25 19:35:15
 */
@Service
@Slf4j
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService {

    @Autowired
    private SysUserService sysUserService;

    @Resource
    private ChartMapper chartMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private ZhiPuAIManager zhiPuAIManager;
    @Autowired
    private ImageService imageService;

    @Autowired
    private DataFileService dataFileService;
    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private MsgLogService msgLogService;

    @Value("${spring.mail.toUser}")
    private String toUser;

    @Value("${spring.mail.username}")
    private String username;
    @Autowired
    private  RestTemplate restTemplate;

    @Autowired
    private ImageCaptionManager imageCaptionManager;
    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }

        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        // 根据查询条件查询
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Long queryUserIdByChartId(Long id) {

        Long userId = chartMapper.queryUserIdByChartId(id);

        return userId;
    }

    public ImageAnalysisResponse analyzeImage(ImageAnalysisRequest request) {

        String content = imageCaptionManager.generateCaptionFromImageUrl(request.getImageUrl());
        Long userId=request.getUserId();
        String key=request.getUserId().toString()+"_img";
        if( StringUtils.isBlank(redisUtils.get(key))){
            redisUtils.set(key,userId.toString(),60L);
        }else{
            emailUtil.sendSimpleEmail(username,"失败告警邮件","请求图片理解频繁失败");
            MsgLog log=new MsgLog();
            log.setContent("请求图片理解频繁失败");
            log.setUserId(userId);
            log.setSubject("失败告警邮件");
            log.setFromAddress(username);
            log.setToAddress(toUser);
            log.setCreateTime(new Date());
            log.setUpdateTime(new Date());
            msgLogService.save(log);
            throw  new BusinessException(500,"请求太频繁，稍等1分钟");
        }

        ImageAi ai=new ImageAi();
        ai.setImageUrl(request.getImageUrl());
        ai.setContent(content);
        ai.setCreateTime(new Date());
        ai.setUserId(userId);
        imageService.save(ai);
        ImageAnalysisResponse imageAnalysisResponse = new ImageAnalysisResponse();
        imageAnalysisResponse.setContent(content);
        imageAnalysisResponse.setSuccess(true);
        return imageAnalysisResponse;
    }

    private String convertImageUrlToBase64(String imageUrl) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    private Map<String, Boolean> buildTasks(ImageAnalysisRequest request) {
        Map<String, Boolean> tasks = new HashMap<>();
        tasks.put("object_detection", request.isObjectDetection());
        tasks.put("scene_understanding", request.isSceneUnderstanding());
        tasks.put("description_generation", request.isGenerateDescription());
        return tasks;
    }


    @Override
    public synchronized BIResponse genChartByAI( GenChartByAIRequest genChartByAIRequest) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String name = genChartByAIRequest.getName();
        String goal = genChartByAIRequest.getGoal();
        String chartType = genChartByAIRequest.getChartType();
        Long userId=genChartByAIRequest.getUserId();
        // 判断能否生成图表  redis限制1min/30秒 生成一个
        String key=userId.toString()+"_data";
        if( StringUtils.isBlank(redisUtils.get(key))){
            redisUtils.set(key,userId.toString(),30L);
        }else{
            saveAndReturnFailedChart(name, goal, chartType, null, "",
                    "", "请求频繁失败", userId);
            emailUtil.sendSimpleEmail(username,"失败告警邮件","请求频繁失败");
            MsgLog log=new MsgLog();
            log.setContent("请求频繁失败");
            log.setUserId(userId);
            log.setSubject("失败告警邮件");
            log.setFromAddress(username);
            log.setToAddress(toUser);
            log.setCreateTime(new Date());
            log.setUpdateTime(new Date());
            msgLogService.save(log);
//            throw  new BusinessException(500,"请求太频繁，稍等1分钟");
            throw  new BusinessException(500,"请求太频繁，稍等30秒");
        }

        // 校验参数
        DataFile dataFile = dataFileService.getById(genChartByAIRequest.getDataId());

        InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(dataFile.getUrl())
                        .build());
// 修改这部分：处理选定的列
        String csvData;
        if (genChartByAIRequest.getSelectedColumns() != null && !genChartByAIRequest.getSelectedColumns().isEmpty()) {
            // 只提取选定的列
            csvData = ExcelUtils.excelToCsv(inputStream, genChartByAIRequest.getSelectedColumns());
        } else {
            // 使用所有数据
            csvData = ExcelUtils.excelToCsv(inputStream);
        }
         // 构造用户输入
         StringBuilder userInput = new StringBuilder("你是一个数据分析刊师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n");
                 userInput.append("分析需求：").append("\n");
                 userInput.append(goal).append("\n");
                if (StringUtils.isNotBlank(chartType)) {
                    userInput.append(goal).append(",请注意图表类型为").append("\n");
                    userInput.append(chartType).append("\n");
                }
                userInput.append("原始数据：").append("\n");
                //两部分
                userInput.append(csvData).append("\n");
                userInput.append("请根据这两部分内容，按照以下格式生成内容（此外不要输出任何多余的开头、结尾、注释）").append("\n");
                userInput.append("第1部分【【【【【【").append("\n");
                userInput.append("{前端Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化}").append("\n");
                userInput.append("】】】】】】").append("\n");
                userInput.append("第2部分【【【【【【").append("\n");
                userInput.append("{明确的数据分析结论、越详细越好，不要生成多余的注释}").append("\n");

        log.info("用户输入诉求：{}", userInput);
        String response = null;
        try {
            response = zhiPuAIManager.doChat(new ChatMessage(ChatMessageRole.USER.value(), userInput.toString()));
            response=response.replace("option =","").replace("option:","");
            emailUtil.sendSimpleEmail(username,"生成成功邮件",response);
            MsgLog log=new MsgLog();
            log.setContent(response);
            log.setSubject("生成成功邮件");
            log.setFromAddress(username);
            log.setToAddress(toUser);
            log.setUserId(userId);
            log.setCreateTime(new Date());
            log.setUpdateTime(new Date());
            msgLogService.save(log);
        } catch (Exception e) {
            saveAndReturnFailedChart(name, goal, chartType, csvData, "",
                    "", "智谱AI调用失败", userId);
            emailUtil.sendSimpleEmail(username,"失败告警邮件",e.getMessage());
            MsgLog log=new MsgLog();
            log.setContent(response);
            log.setUserId(userId);
            log.setSubject("失败告警邮件");
            log.setFromAddress(username);
            log.setToAddress(toUser);
            log.setCreateTime(new Date());
            log.setUpdateTime(new Date());
            msgLogService.save(log);
        }
        //1、解析分析结果
        String genResult = AIUtil.extractAnalysis(response).trim();
        //1、解析js代码
        String genChart = AIUtil.extractJsCode(response);
        genChart = ChartUtil.optimizeGenChart(genChart);
        log.info("生成的数据结论：" + genResult);
        log.info("生成的JS代码：" + genChart);
        boolean isValid = ChartUtil.isChartValid(genChart);
        log.info("生成的Echarts代码是否合法：{}", isValid);
        if (!isValid) {
            this.saveAndReturnFailedChart(name, goal, chartType, csvData, genChart,
                    genResult, "生成的JS代码不合法", userId);
            throw new BusinessException(500, "图表生成失败");
        }
        genChart = ChartUtil.strengthenGenChart(genChart);

        // 插入数据到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(userId);
        chart.setStatus("succeed");
        boolean saveResult = this.save(chart);

        log.info("图表 Id 为：{} 的对象信息: {}", chart.getId(), chart.toString());
        BIResponse biResponse = new BIResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        return biResponse;
    }

    @Override
    public Page<Chart> searchMyCharts(ChartQueryRequest chartQueryRequest) {
        String name = chartQueryRequest.getName();
        long current = chartQueryRequest.getCurrent();
        long pageSize = chartQueryRequest.getPageSize();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        queryWrapper.and(qw -> qw.like(StringUtils.isNotBlank(name), "name", name).or().like(StringUtils.isNotBlank(name), "chartType", name));
        Page<Chart> page = this.page(new Page<>(current, pageSize), queryWrapper);
        return page;
    }

    @Override
    public void handleChartUpdateSuccess(Long chartId, String genChart, String genResult) {
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setStatus("succeed");
        boolean updateResult = this.updateById(chart);
        if (!updateResult) {
            log.error("图表Id: {} 更新状态为成功失败了", chartId);
        }
    }

    @Override
    public void handleChartUpdateError(Long chartId, String execMessage) {
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setStatus("failed");
        chart.setExecMessage(execMessage);
        boolean updateResult = this.updateById(chart);
        if (!updateResult) {
            log.error("更新图表状态为失败失败了" + chartId + "," + execMessage);
        }
    }


    /**
     * 创建并返回一个状态为失败的图表，并将相关信息落表
     *
     * @param name
     * @param goal
     * @param chartType
     * @param chartData
     * @param genResult
     * @param execMessage
     * @param userId
     */
    @Override
    public void saveAndReturnFailedChart(String name, String goal, String chartType,
                                         String chartData, String genChart, String genResult, String execMessage, Long userId) {
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setChartData(chartData);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setExecMessage(execMessage);
        chart.setUserId(userId);
        chart.setStatus("failed");
        this.save(chart);
    }



    @Override
    public Boolean updateChartByAdmin(Chart chart) {
        return this.updateById(chart);
    }

    @Override
    public Page<Chart> pageChart(ChartQueryRequest chartQueryRequest) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        String searchParams = chartQueryRequest.getSearchParams();
        // 限制爬虫
        QueryWrapper<Chart> queryWrapper = this.getQueryWrapper(chartQueryRequest);
        queryWrapper.like(StringUtils.isNotEmpty(searchParams), "name", searchParams).or(StringUtils.isNotEmpty(searchParams), wrapper -> wrapper.like("status", searchParams));
        queryWrapper.orderBy(true, true, "updateTime");
        Page<Chart> chartPage = this.page(new Page<>(current, size), queryWrapper);
        return chartPage;
    }




}
