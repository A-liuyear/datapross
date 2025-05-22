package com.admin.web.data.mapper;

import com.admin.web.data.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author hejiajun
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2024-01-25 19:35:15
* @Entity com.hjj.smart.model.entity.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {
    List<Map<String, Object>> queryChartData(@Param("querySql") String querySql);

    Long queryUserIdByChartId(@Param("id") Long id);
}