package com.admin.web.data.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 图标名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 图表原始数据
     */
    private String chartData;

    /**
     * 分析结果
     */
    private String genResult;

    /**
     * 图表状态
     */
    private String status;

    /**
     * 创建的userId
     */
    private Long userId;

    /**
     * 搜索关键词
     */
    private String searchParams;

    private Long teamId;

    private static final long serialVersionUID = 1L;
}