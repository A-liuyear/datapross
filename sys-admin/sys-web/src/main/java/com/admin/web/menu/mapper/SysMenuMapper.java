package com.admin.web.menu.mapper;

import com.admin.web.menu.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> getMenuByUserId(@Param("userId") Long userId);

    List<SysMenu> getMenuByRoleId(@Param("roleId") Long roleId);


}
