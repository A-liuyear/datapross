package com.admin.web.data.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("t_data_file")
public class DataFile {
    @TableId(type = IdType.AUTO)
    private Long dataId;
    private String fileType;
    private String fileName;
    private Double price;
    private String remark;
    private String url;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String imageUrl;

    @TableField(exist = false)
    private Double rating=0.0;

    @TableField(exist = false)
    private Long reviewCount=0L;

    private  String category;

    private Long dataTotal;

    private Long userId;
    @TableField(exist = false)
    private String showFlag;

    @TableField(exist = false)
    private Long evateCount;

    @TableField(exist = false)
    private Double avgScore;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date maxCreateTime;

    private String status;
}
