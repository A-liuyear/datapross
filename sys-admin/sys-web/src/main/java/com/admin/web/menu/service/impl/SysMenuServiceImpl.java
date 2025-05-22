package com.admin.web.menu.service.impl;

import com.admin.web.menu.entity.MakeMenuTree;
import com.admin.web.menu.entity.MenuTreeParam;
import com.admin.web.menu.entity.MenuTreeVo;
import com.admin.web.menu.entity.SysMenu;
import com.admin.web.menu.mapper.SysMenuMapper;
import com.admin.web.menu.service.SysMenuService;
import com.admin.web.user.entity.SysUser;
import com.admin.web.user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {


    @Override
    public List<SysMenu> parentMenus() {
        String[] type={"0","1"}; //0 目录 1 菜单 2 按钮
        List<String> strings= Arrays.asList(type);
        QueryWrapper<SysMenu> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().in(SysMenu::getType,strings).orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> sysMenus = this.baseMapper.selectList(queryWrapper);
        //组装顶级菜单
        SysMenu menu=new SysMenu();
        menu.setTitle("顶级菜单");
        menu.setLabel("顶级菜单");
        menu.setParentId(-1L);
        menu.setMenuId(0L);
        menu.setValue(0L);
        sysMenus.add(menu);
        //组装菜单树
        List<SysMenu> tree = MakeMenuTree.makeTree(sysMenus, -1L);
        return tree;
    }

    @Override
    public List<SysMenu> getMenuByUserId(Long userId) {
        return this.baseMapper.getMenuByUserId(userId);
    }

    @Override
    public List<SysMenu> getMenuByRoleId(Long roleId) {
        return this.baseMapper.getMenuByRoleId(roleId);
    }


}
