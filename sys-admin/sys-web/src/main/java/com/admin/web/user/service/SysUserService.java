package com.admin.web.user.service;

import com.admin.web.menu.entity.MenuTreeParam;
import com.admin.web.menu.entity.MenuTreeVo;
import com.admin.web.user.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;


public interface SysUserService extends IService<SysUser> {
    //保存角色
    void saveUser(SysUser sysUser);

    void editUser(SysUser sysUser);

    void deleteUser(Long userId);

    MenuTreeVo getMenuTreeVo(MenuTreeParam menuTreeParam);
}
