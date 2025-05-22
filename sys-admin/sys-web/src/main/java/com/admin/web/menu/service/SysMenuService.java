package com.admin.web.menu.service;

import com.admin.web.menu.entity.MenuTreeParam;
import com.admin.web.menu.entity.MenuTreeVo;
import com.admin.web.menu.entity.SysMenu;
import com.admin.web.role.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> parentMenus();

    List<SysMenu> getMenuByUserId(Long userId);

    List<SysMenu> getMenuByRoleId(Long roleId);


}
