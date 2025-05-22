package com.admin.web.sysRoleMenu.service.impl;

import com.admin.web.sysRoleMenu.entity.MenuParam;
import com.admin.web.sysRoleMenu.entity.RoleMenu;
import com.admin.web.sysRoleMenu.mapper.SysRoleMenuMapper;
import com.admin.web.sysRoleMenu.service.SysRoleMenuService;
import com.admin.web.sysUserRole.entity.SysUserRole;
import com.admin.web.sysUserRole.mapper.SysUserRoleMapper;
import com.admin.web.sysUserRole.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, RoleMenu> implements SysRoleMenuService {

    @Override
    @Transactional
    public void saveRoleMenu(MenuParam menuParam) {
        QueryWrapper<RoleMenu> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(RoleMenu::getRoleId,menuParam.getRoleId());
        this.baseMapper.delete(queryWrapper);
        this.baseMapper.saveRoleMenu(menuParam.getRoleId(),menuParam.getList());
    }
}
