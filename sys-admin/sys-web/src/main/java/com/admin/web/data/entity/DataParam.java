package com.admin.web.data.entity;

import com.admin.common.BasePage;
import lombok.Data;

import java.util.List;

@Data
public class DataParam  extends BasePage {
    private String dataId;
    private String fileName;
    private String searchText;
    private String category;
    private Long priceMin;
    private Long priceMax;
    private String dateStart;
    private String dateEnd;

    private Double rating;

    private String sortBy;

    private String from;
}
