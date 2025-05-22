package com.admin.web.data.entity.ai;

import lombok.Data;

/**
 * BI返回结果
 */
@Data
public class BIResponse {
    private long chartId;
    private String genChart;
    private String genResult;
}
