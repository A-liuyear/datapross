package com.admin.web.data.service;

import com.admin.web.data.entity.*;
import com.admin.web.data.entity.ai.BIResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ChartService extends IService<Chart> {
    public Boolean updateChartByAdmin(Chart chart);
    public Page<Chart> pageChart(ChartQueryRequest chartQueryRequest);
    public void saveAndReturnFailedChart(String name, String goal, String chartType,
                                         String chartData, String genChart, String genResult, String execMessage, Long userId);
    public void handleChartUpdateError(Long chartId, String execMessage);
    public void handleChartUpdateSuccess(Long chartId, String genChart, String genResult);
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);
    public Page<Chart> searchMyCharts(ChartQueryRequest chartQueryRequest);
    public Long queryUserIdByChartId(Long id);
    public  BIResponse genChartByAI( GenChartByAIRequest genChartByAIRequest) throws Exception;
    public ImageAnalysisResponse analyzeImage(ImageAnalysisRequest request);
}
