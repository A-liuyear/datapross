package com.admin.web.data.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GenChartByAIRequest implements Serializable{
    private String name;

    private String goal;

    private String chartType;

    private Long dataId;

    private Long userId;

    private List<String> selectedColumns;
}
