package com.admin.web.data.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@HeadStyle( // 表头样式
        horizontalAlignment = HorizontalAlignmentEnum.CENTER, // 水平居中
        verticalAlignment = VerticalAlignmentEnum.CENTER    // 垂直居中
)
@ContentStyle( // 内容样式
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        verticalAlignment = VerticalAlignmentEnum.CENTER
)
@Data
@TableName("t_data_order")
public class Order   {
    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    private Long id;
    @ColumnWidth(20) // 设置列宽
    @ExcelProperty("数据编号")
    private Long dataId;
    @ExcelIgnore
    private Long userId;
    @ExcelProperty("文件名称")
    @ColumnWidth(20) // 设置列宽
    private String dataName;
    @ExcelProperty("积分数")
    private Long payAmount;
    @ColumnWidth(20) // 设置列宽
    @ExcelProperty("支付/赚取积分")
    private String pointType;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(30)
    @ExcelProperty("创建时间")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("更新时间")
    @ColumnWidth(30)
    private Date updateTime;


}
