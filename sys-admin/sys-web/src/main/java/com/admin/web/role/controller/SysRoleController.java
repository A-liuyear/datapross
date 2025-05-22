package com.admin.web.role.controller;

import com.admin.common.util.ResultUtils;
import com.admin.common.util.ResultVo;
import com.admin.web.role.entity.RoleParam;
import com.admin.web.role.entity.SelectItem;
import com.admin.web.role.entity.SysRole;
import com.admin.web.role.service.SysRoleService;
import com.admin.web.sysRoleMenu.entity.MenuParam;
import com.admin.web.sysRoleMenu.service.SysRoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/role")
@RestController
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @PostMapping
    public ResultVo add(@RequestBody SysRole sysRole) {
        sysRole.setCreateTime(new Date());
        sysRole.setUpdateTime(new Date());
        if(sysRoleService.save(sysRole)){
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    @PutMapping
    public ResultVo update(@RequestBody SysRole sysRole) {
        sysRole.setUpdateTime(new Date());
        if(sysRoleService.updateById(sysRole)){
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }
    @DeleteMapping("/{roleId}")
    public ResultVo delete(@PathVariable("roleId") Long roleId) {
        if(sysRoleService.removeById(roleId)){
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    @GetMapping("/list")
    public ResultVo list(RoleParam roleParam){
        IPage<SysRole> page=new Page<>(roleParam.getCurrentPage(),roleParam.getPageSize());
        QueryWrapper<SysRole> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotEmpty(roleParam.getRoleName())){
            queryWrapper.lambda().like(SysRole::getRoleName,roleParam.getRoleName());
        }
        queryWrapper.lambda().orderByDesc(SysRole::getUpdateTime);
        IPage<SysRole> list = sysRoleService.page(page, queryWrapper);
        return ResultUtils.success("查询成功",list);
    }


    @GetMapping("/selectList")
    public ResultVo selectList(){
        List<SysRole> list = sysRoleService.list();
        List<SelectItem> selectItems=new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item->{
                    SelectItem selectItem=new SelectItem();
                    selectItem.setCheck(false);
                    selectItem.setLabel(item.getRoleName());
                    selectItem.setValue(item.getRoleId());
                    selectItems.add(selectItem);
                });
        return ResultUtils.success("查询成功",selectItems);
    }

    @PostMapping("/saveRoleMenu")
    public ResultVo saveRoleMenu(@RequestBody MenuParam menuParam) {
        sysRoleMenuService.saveRoleMenu(menuParam);
        return ResultUtils.success("新增成功");
    }

}
