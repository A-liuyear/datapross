package com.admin.web.data.entity;

import com.admin.common.BasePage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class EvaluateParam extends BasePage {
    private Long id;
    private Long dataId;
    private Long createUser;
    private String remark;
    private Integer score;
    private String createName;
    private String dataName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String startTime;
    private String endTime;

}
