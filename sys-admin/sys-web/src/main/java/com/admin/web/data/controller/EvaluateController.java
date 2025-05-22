package com.admin.web.data.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.admin.common.util.Constant;
import com.admin.common.util.GetMonthFirstLastDay;
import com.admin.common.util.ResultUtils;
import com.admin.common.util.ResultVo;
import com.admin.config.exception.BusinessException;
import com.admin.config.redis.RedisUtils;
import com.admin.web.data.entity.DataFile;
import com.admin.web.data.entity.DataParam;
import com.admin.web.data.entity.Evaluate;
import com.admin.web.data.entity.EvaluateParam;
import com.admin.web.data.service.DataFileService;
import com.admin.web.data.service.EvaluateService;
import com.admin.web.user.entity.SysUser;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图表接口
 */
@RestController
@RequestMapping("/api/evaluate")
@Slf4j
public class EvaluateController {
    @Autowired
    private RedisUtils redisUtils;
    @Resource
    private EvaluateService evaluateService;

    @Autowired
    private DataFileService dataFileService;
    /**
     * 创建
     *
     * @return
     */
    @PostMapping("/add")
    public ResultVo<Long> addEvaluate(@RequestBody Evaluate evaluate) {
        if (evaluate == null) {
            throw new BusinessException(500,"参数错误");
        }
        evaluate.setCreateTime(new Date());
        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser= JSON.parseObject(user,SysUser.class);
        evaluate.setCreateUser(sysUser.getUserId());
        evaluate.setCreateName(sysUser.getNickName());
        DataFile dataFile = dataFileService.getById(evaluate.getDataId());
        evaluate.setDataName(dataFile.getFileName());
        evaluateService.save(evaluate);
        return ResultUtils.success();
    }


    @GetMapping("/list")
    public ResultVo list(EvaluateParam dataParam){
        IPage<Evaluate> page=new Page<>(dataParam.getCurrentPage(),dataParam.getPageSize());
        QueryWrapper<Evaluate> queryWrapper=new QueryWrapper<>();
        if(dataParam.getDataId()!=null){
            queryWrapper.lambda().eq(Evaluate::getDataId,dataParam.getDataId());
        }
        if(StringUtils.isNotEmpty(dataParam.getRemark())){
            queryWrapper.lambda().like(Evaluate::getRemark,dataParam.getRemark());
        }
        if(dataParam.getScore()!=null){
            queryWrapper.lambda().eq(Evaluate::getScore,dataParam.getScore());
        }
        if(StringUtils.isNotEmpty(dataParam.getStartTime())){
            queryWrapper.lambda().gt(Evaluate::getCreateTime,dataParam.getStartTime());
        }
        if(StringUtils.isNotEmpty(dataParam.getEndTime())){
            queryWrapper.lambda().lt(Evaluate::getCreateTime,dataParam.getEndTime());
        }
        queryWrapper.lambda().orderByDesc(Evaluate::getCreateTime);
        IPage<Evaluate> list = evaluateService.page(page, queryWrapper);

        return ResultUtils.success("查询成功",list);
    }
    @GetMapping("/evaluateList")
    public ResultVo evaluateList(EvaluateParam dataParam){
        QueryWrapper<Evaluate> queryWrapper=new QueryWrapper<>();

        Map<String,Object> objectMap=new HashMap<>();
        List<Evaluate> list = evaluateService.list(queryWrapper);
        List<Double> result=new ArrayList<>();
        double scoreRate=0.0;
        if(!CollectionUtils.isEmpty(list)){
            Map<Integer, List<Evaluate>> collect = list.stream().collect(Collectors.groupingBy(Evaluate::getScore));
            int fiveScore=collect.get(5)==null?0:collect.get(5).size();
            int fourScore=collect.get(4)==null?0:collect.get(4).size();
            int threeScore=collect.get(3)==null?0:collect.get(3).size();
            int twoScore=collect.get(2)==null?0:collect.get(2).size();
            int oneScore=collect.get(1)==null?0:collect.get(1).size();
            Long total=Long.valueOf(fiveScore+fourScore+threeScore+twoScore+oneScore);
            result.add(calculatePercentage(fiveScore,total));
            result.add(calculatePercentage(fourScore,total));
            result.add(calculatePercentage(threeScore,total));
            result.add(calculatePercentage(twoScore,total));
            result.add(calculatePercentage(oneScore,total));


            int fiveScoreData=collect.get(5)==null?0:collect.get(5).size()*5;
            int fourScoreData=collect.get(4)==null?0:collect.get(4).size()*4;
            int threeScoreData=collect.get(3)==null?0:collect.get(3).size()*3;
            int twoScoreData=collect.get(2)==null?0:collect.get(2).size()*2;;
            int oneScoreData=collect.get(1)==null?0:collect.get(1).size();
            scoreRate=calculatePercentage(fiveScoreData+fourScoreData+threeScoreData+twoScoreData+oneScoreData,total)/100;

        }
        objectMap.put("dataList",result);
        objectMap.put("scoreRate",scoreRate);
        return ResultUtils.success("查询成功",objectMap);
    }


    @GetMapping("/statistic")
    public ResultVo statistic(DataParam dataParam){
        QueryWrapper<Evaluate> queryWrapper=new QueryWrapper<>();
        Map<String,Object> objectMap=new HashMap<>();
        List<Evaluate> list = evaluateService.list(queryWrapper);
        String start= GetMonthFirstLastDay.getFirstDayOfMonth();
        String end= GetMonthFirstLastDay.getLastDayOfMonth();

        queryWrapper.lambda().gt(Evaluate::getCreateTime,start);
        queryWrapper.lambda().lt(Evaluate::getCreateTime,end);
        List<Evaluate> addList = evaluateService.list(queryWrapper);
        Integer addTotal=addList.size();
        double scoreRate=0.0;
        List<Integer>  evaTotal=new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            Map<Integer, List<Evaluate>> collect = list.stream().collect(Collectors.groupingBy(Evaluate::getScore));
            int fiveScore=collect.get(5)==null?0:collect.get(5).size();
            int fourScore=collect.get(4)==null?0:collect.get(4).size();
            int threeScore=collect.get(3)==null?0:collect.get(3).size();
            int twoScore=collect.get(2)==null?0:collect.get(2).size();
            int oneScore=collect.get(1)==null?0:collect.get(1).size();
            evaTotal.add(oneScore);
            evaTotal.add(twoScore);
            evaTotal.add(threeScore);
            evaTotal.add(fourScore);
            evaTotal.add(fiveScore);

            Long total=Long.valueOf(fiveScore+fourScore+threeScore+twoScore+oneScore);

            int fiveScoreData=collect.get(5)==null?0:collect.get(5).size()*5;
            int fourScoreData=collect.get(4)==null?0:collect.get(4).size()*4;
            int threeScoreData=collect.get(3)==null?0:collect.get(3).size()*3;
            int twoScoreData=collect.get(2)==null?0:collect.get(2).size()*2;;
            int oneScoreData=collect.get(1)==null?0:collect.get(1).size();
            scoreRate=calculatePercentage(fiveScoreData+fourScoreData+threeScoreData+twoScoreData+oneScoreData,total)/100;

        }else{
            evaTotal.add(0);
            evaTotal.add(0);
            evaTotal.add(0);
            evaTotal.add(0);
            evaTotal.add(0);
        }
        objectMap.put("totalEvaluate",list.size());
        objectMap.put("scoreRate",scoreRate);
        objectMap.put("addTotal",addTotal);
        objectMap.put("evaTotal",evaTotal);
        objectMap.put("satisfaction",calculatePercentage(scoreRate,5));
        return ResultUtils.success("查询成功",objectMap);
    }


    /**
     * 计算 value 在 total 中的占比（百分比形式）
     * @param value 要计算的值
     * @param total 总和
     * @return 占比百分比（例如 25.0 表示 25.0%）
     */
    public static double calculatePercentage(double value, double total) {
        if (total == 0) {
            return 0.0; // 避免除以 0
        }
        return (value*100 / total);
    }
}
