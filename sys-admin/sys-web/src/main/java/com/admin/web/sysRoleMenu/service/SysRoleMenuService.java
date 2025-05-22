package com.admin.web.sysRoleMenu.service;

import com.admin.web.sysRoleMenu.entity.MenuParam;
import com.admin.web.sysRoleMenu.entity.RoleMenu;
import com.admin.web.sysUserRole.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SysRoleMenuService extends IService<RoleMenu> {
    void saveRoleMenu(MenuParam menuParam);
}
