package com.admin.web.menu.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class MenuTreeVo {
    private List<SysMenu> menuList=new ArrayList<SysMenu>();
    private Object[] checkList;
}
