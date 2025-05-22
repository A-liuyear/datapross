package com.admin.config.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.admin.config.exception.BusinessException;
import com.admin.web.menu.entity.SysMenu;
import com.admin.web.menu.service.SysMenuService;
import com.admin.web.user.entity.SysUser;
import com.admin.web.user.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;


    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        SysUser user=sysUserService.getById((Serializable) loginId);
        List<SysMenu> menuList=null;
        if(StringUtils.isNotEmpty(user.getIsAdmin())&&"1".equals(user.getIsAdmin())){
            menuList=sysMenuService.list();
        }else{
            menuList=sysMenuService.getMenuByUserId(user.getUserId());
        }
        if(menuList.stream().filter(Objects::nonNull).collect(Collectors.toList()).isEmpty()){
            throw new BusinessException(500,"该用户对应的角色未分配相应权限");
        }
        List<String> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item!=null&&StringUtils.isNotEmpty(item.getCode()))
                .map(item -> item.getCode())
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return List.of();
    }
}
