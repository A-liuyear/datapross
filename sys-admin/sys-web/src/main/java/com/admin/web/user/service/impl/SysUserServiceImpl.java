package com.admin.web.user.service.impl;

import com.admin.web.menu.entity.MakeMenuTree;
import com.admin.web.menu.entity.MenuTreeParam;
import com.admin.web.menu.entity.MenuTreeVo;
import com.admin.web.menu.entity.SysMenu;
import com.admin.web.menu.service.SysMenuService;
import com.admin.web.sysUserRole.entity.SysUserRole;
import com.admin.web.sysUserRole.service.SysUserRoleService;
import com.admin.web.user.entity.SysUser;
import com.admin.web.user.mapper.SysUserMapper;
import com.admin.web.user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysMenuService sysMenuService;
    @Override
    @Transactional
    public void saveUser(SysUser sysUser) {
        int inserted = this.baseMapper.insert(sysUser);
        if(inserted>0){
            String[] split = sysUser.getRoleId().split(",");
            if(split.length>0){
                List<SysUserRole> roles=new ArrayList<>();
                for (String roleId : split) {
                    SysUserRole sysUserRole=new SysUserRole();
                    sysUserRole.setRoleId(Long.valueOf(roleId));
                    sysUserRole.setUserId(sysUser.getUserId());
                    roles.add(sysUserRole);
                }
                sysUserRoleService.saveBatch(roles);
            }
        }
    }

    @Override
    @Transactional
    public void editUser(SysUser sysUser) {
        int inserted = this.baseMapper.updateById(sysUser);
        if(inserted>0){
            String[] split = sysUser.getRoleId().split(",");
            QueryWrapper<SysUserRole> queryWrapper=new QueryWrapper<>();
            queryWrapper.lambda().eq(SysUserRole::getUserId, sysUser.getUserId());
            sysUserRoleService.remove(queryWrapper);
            if(split.length>0){
                List<SysUserRole> roles=new ArrayList<>();
                for (String roleId : split) {
                    SysUserRole sysUserRole=new SysUserRole();
                    sysUserRole.setRoleId(Long.valueOf(roleId));
                    sysUserRole.setUserId(sysUser.getUserId());
                    roles.add(sysUserRole);
                }
                sysUserRoleService.saveBatch(roles);
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        int i = this.baseMapper.deleteById(userId);
        if(i>0){
            QueryWrapper<SysUserRole> queryWrapper=new QueryWrapper<>();
            queryWrapper.lambda().eq(SysUserRole::getUserId, userId);
            sysUserRoleService.remove(queryWrapper);

        }
    }

    @Override
    public MenuTreeVo getMenuTreeVo(MenuTreeParam menuTreeParam) {

        SysUser sysUser = this.baseMapper.selectById(menuTreeParam.getUserId());
        List<SysMenu> menuList=null;
        if("1".equals(sysUser.getIsAdmin())){
            //超级管理员，查询所有的菜单
            menuList=this.sysMenuService.list();
        }else{
            menuList=this.sysMenuService.getMenuByUserId(sysUser.getUserId());
        }
        //组装树
        List<SysMenu> makeTree = MakeMenuTree.makeTree(menuList, 0L);
        //查询角色原来的菜单
        List<SysMenu> menus = sysMenuService.getMenuByRoleId(menuTreeParam.getRoleId());
        List<Long> ids=new ArrayList<>();
        Optional.ofNullable(menus).orElse(new ArrayList<>())
                .stream()
                .filter(item->item!=null)
                .forEach(item->{
                    ids.add(item.getMenuId());
                });
        MenuTreeVo vo=new MenuTreeVo();
        vo.setMenuList(makeTree);
        vo.setCheckList(ids.toArray());
        return vo;
    }
}
