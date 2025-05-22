package com.admin.web.role.service.impl;

import com.admin.web.role.entity.SysRole;
import com.admin.web.role.mapper.SysRoleMapper;
import com.admin.web.role.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}
