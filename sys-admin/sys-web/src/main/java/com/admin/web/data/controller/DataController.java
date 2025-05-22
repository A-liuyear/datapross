package com.admin.web.data.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.admin.common.util.AvatarUtil;
import com.admin.common.util.Constant;
import com.admin.common.util.ResultUtils;
import com.admin.common.util.ResultVo;
import com.admin.config.minio.MinioConfig;
import com.admin.config.minio.MinioUtils;
import com.admin.config.redis.RedisUtils;
import com.admin.web.data.entity.DataFile;
import com.admin.web.data.entity.DataParam;
import com.admin.web.data.entity.Evaluate;
import com.admin.web.data.entity.Order;
import com.admin.web.data.mapper.DataFileMapper;
import com.admin.web.data.service.DataFileService;
import com.admin.web.data.service.EvaluateService;
import com.admin.web.data.service.ExcelService;
import com.admin.web.data.service.OrderService;
import com.admin.web.user.entity.SysUser;
import com.admin.web.user.service.SysUserService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/api/data")
@RestController
public class DataController {
    @Autowired
    private DataFileService dataFileService;
    @Autowired
    private OrderService orderService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private EvaluateService evaluateService;
    @Resource
    private DataFileMapper dataFileMapper;

    @Resource
    private SysUserService sysUserService;

    @PostMapping("/payData")
    public ResultVo add(@RequestBody Order order) {
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser=JSON.parseObject(user,SysUser.class);
        order.setUserId(sysUser.getUserId());

        DataFile dataFile = dataFileService.getById(order.getDataId());

        SysUser byId = sysUserService.getById(sysUser.getUserId());
        if(byId.getPointNum()==null||byId.getPointNum()<dataFile.getPrice()){
            return ResultUtils.error("积分不足,请联系管理员充积分(admin@163.com)");
        }

        order.setDataName(dataFile.getFileName());
        order.setPointType("支出");
        orderService.save(order);


        QueryWrapper<SysUser> update1=new QueryWrapper<>();
        update1.lambda().eq(  SysUser::getUserId,sysUser.getUserId());
        sysUser.setPointNum(byId.getPointNum()-order.getPayAmount());
        sysUserService.update(sysUser,update1);

        QueryWrapper<SysUser> update2=new QueryWrapper<>();
        update2.lambda().eq(SysUser::getUserId,dataFile.getUserId());
        SysUser user1 = sysUserService.getById(dataFile.getUserId());
        user1.setPointNum(user1.getPointNum()==null?0:user1.getPointNum()+order.getPayAmount());
        sysUserService.update(user1,update2);
        Order order2=new Order();
        order2.setCreateTime(new Date());
        order2.setUpdateTime(new Date());
        order2.setPointType("赚取");
        order.setUserId(dataFile.getUserId());
        order.setDataName(dataFile.getFileName());
        return ResultUtils.success("购买成功");
    }


    @GetMapping("/columns/{dataId}")
    public ResultVo getDataColumns(@PathVariable("dataId") String dataId) {
        // 根据dataId从数据库或文件获取列名
        DataFile dataFile = dataFileService.getById(dataId);
        List<String> columns = excelService.getColumns(dataFile.getUrl());
        return ResultUtils.success("查询成功",columns);
    }

    @PostMapping("/getMyData")
    public ResultVo getMyData() {

        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser=JSON.parseObject(user,SysUser.class);
        QueryWrapper<DataFile> dataQuery=new QueryWrapper<>();
        dataQuery.lambda().eq(DataFile::getUserId,sysUser.getUserId());
        List<DataFile> dataFiles = dataFileService.list(dataQuery);

        QueryWrapper<Order> orderQuery=new QueryWrapper<>();
        orderQuery.lambda().eq(Order::getUserId,sysUser.getUserId());
        List<Order> orders = orderService.list(orderQuery);
        List<DataFile> result=new ArrayList<>();
        if(!CollectionUtils.isEmpty(orders)){
            orders.stream().forEach(a->{
                DataFile file=new DataFile();
                file.setDataId(a.getDataId());
                file.setFileName(a.getDataName());
                result.add(file);
            });
        }
        if(!CollectionUtils.isEmpty(dataFiles)){
            result.addAll(dataFiles);
        }
        return ResultUtils.success("查询成功",result);
    }


    @PostMapping
    public ResultVo add(@RequestBody DataFile dataFile) {
        dataFile.setCreateTime(new Date());
        dataFile.setUpdateTime(new Date());
        dataFile.setFileType(dataFile.getUrl().substring(dataFile.getUrl().indexOf(".")+1));
        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser=JSON.parseObject(user,SysUser.class);
        dataFile.setUserId(sysUser.getUserId());
        String photo = AvatarUtil.getRandomAvatar();
        dataFile.setImageUrl(photo);
        dataFileService.saveData(dataFile);
        return ResultUtils.success("新增成功");
    }

    @PutMapping
    public ResultVo update(@RequestBody DataFile dataFile) {
        dataFile.setUpdateTime(new Date());
        dataFileService.editData(dataFile);
        return ResultUtils.success("编辑成功");
    }
    @DeleteMapping("/{dataId}")
    public ResultVo delete(@PathVariable("dataId") Long dataId) {
        dataFileService.deleteData(dataId);
        return ResultUtils.success("删除成功");
    }
    @GetMapping("/{dataId}")
    public ResultVo get(@PathVariable("dataId") Long dataId) {
        DataFile fileServiceById = dataFileService.getById(dataId);
        SaTokenInfo tokenInfo= StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser=JSON.parseObject(user,SysUser.class);
        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(Order::getDataId,dataId);
        queryWrapper.lambda().eq(Order::getUserId,sysUser.getUserId());
        List<Order> list = orderService.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            fileServiceById.setShowFlag("0");
        }
        QueryWrapper<Evaluate> queryWrapper2=new QueryWrapper<>();
        if(dataId!=null){
            queryWrapper2.lambda().eq(Evaluate::getDataId,dataId);
        }
        Map<String,Object> objectMap=new HashMap<>();
        List<Evaluate> dataList = evaluateService.list(queryWrapper2);
        long total=0;
        double scoreRate=0.0;
        if(!CollectionUtils.isEmpty(dataList)){
            Map<Integer, List<Evaluate>> collect = dataList.stream().collect(Collectors.groupingBy(Evaluate::getScore));
            int fiveScore=collect.get(5)==null?0:collect.get(5).size();
            int fourScore=collect.get(4)==null?0:collect.get(4).size();
            int threeScore=collect.get(3)==null?0:collect.get(3).size();
            int twoScore=collect.get(2)==null?0:collect.get(2).size();
            int oneScore=collect.get(1)==null?0:collect.get(1).size();
            total=Long.valueOf(fiveScore+fourScore+threeScore+twoScore+oneScore);

            int fiveScoreData=collect.get(5)==null?0:collect.get(5).size()*5;
            int fourScoreData=collect.get(4)==null?0:collect.get(4).size()*4;
            int threeScoreData=collect.get(3)==null?0:collect.get(3).size()*3;
            int twoScoreData=collect.get(2)==null?0:collect.get(2).size()*2;;
            int oneScoreData=collect.get(1)==null?0:collect.get(1).size();
            scoreRate=calculatePercentage(fiveScoreData+fourScoreData+threeScoreData+twoScoreData+oneScoreData,total)/100;

        }
        objectMap.put("evaluateTotal",total);
        objectMap.put("scoreRate",scoreRate);
        objectMap.put("obj",fileServiceById);
        return ResultUtils.success("查询成功",objectMap);
    }





    @GetMapping("/list")
    public ResultVo list(DataParam dataParam){
        IPage<DataFile> page = new Page<>(dataParam.getCurrentPage(), dataParam.getPageSize());

        MPJLambdaWrapper<DataFile> wrapper = new MPJLambdaWrapper<DataFile>()
                .selectAll(DataFile.class)
                .selectCount(Evaluate::getId, "evateCount")
                .selectAvg(Evaluate::getScore, "avgScore")
                .leftJoin(Evaluate.class, Evaluate::getDataId, DataFile::getDataId)
                .groupBy(DataFile::getDataId);

        if (StringUtils.isNotEmpty(dataParam.getFileName())) {
            wrapper.like(DataFile::getFileName, dataParam.getFileName());
        }
        if (StringUtils.isNotEmpty(dataParam.getSearchText())) {
            wrapper.and(w -> w.like(DataFile::getFileName, dataParam.getSearchText())
                    .or()
                    .like(DataFile::getRemark, dataParam.getSearchText()));
        }
        if (StringUtils.isNotEmpty(dataParam.getCategory())) {
            wrapper.like(DataFile::getCategory, dataParam.getCategory());
        }
        if (StringUtils.isNotEmpty(dataParam.getDateStart())) {
            wrapper.ge(DataFile::getCreateTime, dataParam.getDateStart());
        }
        if (StringUtils.isNotEmpty(dataParam.getDateEnd())) {
            wrapper.le(DataFile::getCreateTime, dataParam.getDateEnd());
        }
        if (dataParam.getPriceMin() != null) {
            wrapper.ge(DataFile::getPrice, dataParam.getPriceMin());
        }
        if (dataParam.getPriceMax() != null) {
            wrapper.le(DataFile::getPrice, dataParam.getPriceMax());
        }

        if (dataParam.getRating() != null&&dataParam.getRating()>0) {
            wrapper.having("avgScore = {0}",  dataParam.getRating());
        }
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        String user = redisUtils.get(Constant.CACHE_USER_PREFIX + ":" + tokenInfo.getTokenValue());
        SysUser sysUser = JSON.parseObject(user, SysUser.class);

        if (!"1".equals(sysUser.getIsAdmin()) && !"index".equals(dataParam.getFrom())) {
            wrapper.eq(DataFile::getUserId, sysUser.getUserId());
        }

        if (StringUtils.isEmpty(dataParam.getSortBy()) || "newest".equalsIgnoreCase(dataParam.getSortBy())) {
            wrapper.selectMax(Evaluate::getCreateTime, "maxCreateTime")
                    .orderByDesc("maxCreateTime");
        } else if ("most_reviews".equalsIgnoreCase(dataParam.getSortBy())) {
            wrapper.orderByDesc("evateCount");
        } else if ("highest_rated".equalsIgnoreCase(dataParam.getSortBy())) {
            wrapper.orderByDesc("avgScore");
        }

        IPage<DataFile> dataFileIPage = dataFileMapper.selectJoinPage(page, DataFile.class, wrapper);
        return ResultUtils.success("查询成功", dataFileIPage);

    }


    @PostMapping("/uploadFile")
    public ResultVo uploadFile(
            @RequestParam MultipartFile file) throws Exception {
        String uploadFile = dataFileService.uploadFile(file);
        return ResultUtils.success("上传成功",uploadFile);
    }

    @GetMapping("/excel")
    public ResponseEntity<ByteArrayResource> getExcelFromMinio(Long id) throws Exception {
        DataFile byId = dataFileService.getById(id);
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(byId.getUrl())
                        .build())) {

            // 将输入流转换为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            byte[] bytes = outputStream.toByteArray();

            // 创建响应
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + byId.getFileName())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (MinioException  | NoSuchAlgorithmException e) {
            throw new Exception("从MinIO获取文件失败", e);
        }
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
