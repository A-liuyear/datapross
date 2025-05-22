package com.admin.web.menu.controller;

import com.admin.common.util.ResultUtils;
import com.admin.common.util.ResultVo;
import com.admin.web.menu.entity.MakeMenuTree;
import com.admin.web.menu.entity.RouterVo;
import com.admin.web.menu.entity.SysMenu;
import com.admin.web.menu.service.SysMenuService;
import com.admin.web.user.entity.SysUser;
import com.admin.web.user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/menu")
@RestController
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserService sysUserService;

    @PostMapping
    public ResultVo add(@RequestBody SysMenu sysMenu) {
        sysMenu.setCreateTime(new Date());
        sysMenu.setUpdateTime(new Date());
        if(sysMenuService.save(sysMenu)){
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    @PutMapping
    public ResultVo update(@RequestBody SysMenu sysMenu) {
        sysMenu.setUpdateTime(new Date());
        if(sysMenuService.updateById(sysMenu)){
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }
    @DeleteMapping("/{menuId}")
    public ResultVo delete(@PathVariable("menuId") Long menuId) {
        QueryWrapper<SysMenu> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(SysMenu::getParentId, menuId);
        List<SysMenu> sysMenus = sysMenuService.list(queryWrapper);
        if(!CollectionUtils.isEmpty(sysMenus)){
            return ResultUtils.error("该菜单存在下级,不能删除");

        }
        if(sysMenuService.removeById(menuId)){
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    @GetMapping("/list")
    public ResultVo list(){
        QueryWrapper<SysMenu> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> list = sysMenuService.list(queryWrapper);
        List<SysMenu> sysMenus = MakeMenuTree.makeTree(list, 0L);
        return ResultUtils.success("查询成功",sysMenus);
    }

    //上级菜单
    @GetMapping("/parentMenus")
    public ResultVo parentMenus(){
        List<SysMenu> list = sysMenuService.parentMenus();
        return ResultUtils.success("查询成功",list);
    }


    @GetMapping("/getMenuList")
    public ResultVo getMenuList(Long userId){
        SysUser sysUser = sysUserService.getById(userId);
        List<SysMenu> menuList=null;
        if("1".equals(sysUser.getIsAdmin())){
            QueryWrapper<SysMenu> queryWrapper=new QueryWrapper<>();
            queryWrapper.lambda().orderByAsc(SysMenu::getOrderNum);
            menuList=sysMenuService.list(queryWrapper);
        }else{
            menuList=sysMenuService.getMenuByUserId(userId);
        }
        //去掉按钮数据
        List<SysMenu> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream().filter(item -> StringUtils.isNotEmpty(item.getType()) && !item.getType().equals("2")).collect(Collectors.toList());
        List<RouterVo> routerVos = MakeMenuTree.makeRouter(collect, 0L);
        return ResultUtils.success("查询成功",routerVos);
    }
}
