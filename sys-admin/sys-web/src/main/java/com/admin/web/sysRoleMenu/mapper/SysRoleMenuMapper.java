package com.admin.web.sysRoleMenu.mapper;

import com.admin.web.sysRoleMenu.entity.RoleMenu;
import com.admin.web.sysUserRole.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMenuMapper extends BaseMapper<RoleMenu> {

    boolean saveRoleMenu(@Param("roleId")Long roleId, @Param("menuIds") List<Long> menuIds);
}
