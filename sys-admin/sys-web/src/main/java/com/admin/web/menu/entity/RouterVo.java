package com.admin.web.menu.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {
    private String path;
    private String component;
    private String name;
    private String redirect;
    private Meta meta;
    private Integer vShow;

    @Data
    @AllArgsConstructor
    public class Meta{
        private String title;
        private String icon;
        private Object[] roles;
    }
    private List<RouterVo> children=new ArrayList<>();
}
