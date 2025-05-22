package com.admin.web.sysRoleMenu.entity;

import lombok.Data;

import java.util.List;

@Data
public class MenuParam {
    private Long roleId;
    private List<Long> list;
}
