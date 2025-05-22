package com.admin.web.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.admin.common.util.Constant;
import com.admin.common.util.MD5Utils;
import com.admin.common.util.ResultUtils;
import com.admin.common.util.ResultVo;
import com.admin.config.jwt.JwtUtils;
import com.admin.config.redis.RedisUtils;
import com.admin.web.menu.entity.MenuTreeParam;
import com.admin.web.menu.entity.MenuTreeVo;
import com.admin.web.menu.entity.SysMenu;
import com.admin.web.menu.service.SysMenuService;
import com.admin.web.sysUserRole.entity.SysUserRole;
import com.admin.web.sysUserRole.service.SysUserRoleService;
import com.admin.web.user.entity.*;
import com.admin.web.user.service.SysUserService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/api/user")
@RestController
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private RedisUtils redisUtils;



    @PostMapping
    @SaCheckPermission("sys:user:add")
    public ResultVo add(@RequestBody SysUser sysUser) {
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        sysUser.setPassword(MD5Utils.encode(sysUser.getPassword()));
        sysUserService.saveUser(sysUser);
        return ResultUtils.success("新增成功");
    }

    @PutMapping
    public ResultVo update(@RequestBody SysUser sysUser) {
        sysUser.setUpdateTime(new Date());
        sysUser.setPassword(null);
        sysUserService.editUser(sysUser);
        return ResultUtils.success("编辑成功");
    }
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Long userId) {
        sysUserService.deleteUser(userId);
        return ResultUtils.success("删除成功");
    }

    @PostMapping("/{userId}")
    public ResultVo getUserById(@PathVariable("userId") Long userId) {
        SysUser user = sysUserService.getById(userId);
        return ResultUtils.success("查询成功",user);
    }

    @GetMapping("/list")
    public ResultVo list(UserParam userParam){
        IPage<SysUser> page=new Page<>(userParam.getCurrentPage(),userParam.getPageSize());
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotEmpty(userParam.getNickName())){
            queryWrapper.lambda().like(SysUser::getNickName,userParam.getNickName());
        }
        if(StringUtils.isNotEmpty(userParam.getPhone())){
            queryWrapper.lambda().like(SysUser::getPhone,userParam.getPhone());
        }
        queryWrapper.lambda().orderByDesc(SysUser::getUpdateTime);
        IPage<SysUser> list = sysUserService.page(page, queryWrapper);
        return ResultUtils.success("查询成功",list);
    }
    @GetMapping("/getRolelist")
    public ResultVo getRoleList(Long userId){
        QueryWrapper<SysUserRole> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRole::getUserId,userId);
        List<SysUserRole> list = sysUserRoleService.list(queryWrapper);
        List<Long> roleList=new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item->{
                    roleList.add(item.getRoleId());
                });
        return ResultUtils.success("查询成功",roleList);
    }

    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@RequestBody SysUser sysUser){
        UpdateWrapper<SysUser> updateWrapper=new UpdateWrapper<>();
        updateWrapper.lambda().eq(SysUser::getUserId,sysUser.getUserId())
                .set(SysUser::getPassword,"123456");
        if(sysUserService.update(updateWrapper)){
            return ResultUtils.success("密码重置成功");
        }else{
            return ResultUtils.error("密码重置失败");
        }
    }
    //图片验证码
    @PostMapping("/getImage")
    public ResultVo imageCode(HttpServletRequest request){
       // HttpSession session = request.getSession();
        //生成验证码
        String text = defaultKaptcha.createText();
        String codeToken=UUID.randomUUID().toString();
        //session.setAttribute("code",text);
        redisUtils.set(codeToken,text,5*60L);
        System.out.println("文字验证码为"+text);
        // 生成图片验证码
        ByteArrayOutputStream out = null;
        BufferedImage image = defaultKaptcha.createImage(text);
        out=new ByteArrayOutputStream();
        try {
            ImageIO.write(image,"jpg",out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String prefix="data:image/jpeg;base64,";
        Map<String,String> map=new HashMap<>();
        map.put("img",prefix+Base64.getEncoder().encodeToString(out.toByteArray()));
        map.put("codeToken",codeToken);

        // 对字节组Base64编码
        return ResultUtils.success("生成成功", map);
    }

  /*  @PostMapping("/login")
    public ResultVo login(HttpServletRequest request,@RequestBody LoginParam loginParam){
        String code = loginParam.getCode();
        //HttpSession session = request.getSession();
        //String varifyCode = (String)session.getAttribute("code");
        String codeToken =loginParam.getCodeToken();
        String varifyCode = redisUtils.get(codeToken);
        if(StringUtils.isBlank(varifyCode)){
            return ResultUtils.error("验证码过期");
        }
        if(!varifyCode.equals(code)){
            return ResultUtils.error("验证码不正确");
        }
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUserName,loginParam.getUserName());
        queryWrapper.lambda().eq(SysUser::getPassword,MD5Utils.encode(loginParam.getPassword()));
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        if(sysUser==null){
            return ResultUtils.error("用户名或密码错误");
        }
        LoginVo vo=new LoginVo();
        vo.setUserId(sysUser.getUserId());
        vo.setNickName(sysUser.getNickName());
        Map<String,String> map=new HashMap<>();
        map.put("userId",sysUser.getUserId().toString());
        String token = jwtUtils.createToken(map);
        vo.setToken(token);
        return ResultUtils.success("登录成功", vo);
    }*/

    @PostMapping("/login")
    public ResultVo login(HttpServletRequest request,@RequestBody LoginParam loginParam){
        String code = loginParam.getCode();
        //HttpSession session = request.getSession();
        //String varifyCode = (String)session.getAttribute("code");
        String codeToken =loginParam.getCodeToken();
        String varifyCode = redisUtils.get(codeToken);
        if(StringUtils.isBlank(varifyCode)){
            return ResultUtils.error("验证码过期");
        }
        if(!varifyCode.equals(code)){
            return ResultUtils.error("验证码不正确");
        }
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUserName,loginParam.getUserName());
        queryWrapper.lambda().eq(SysUser::getPassword,MD5Utils.encode(loginParam.getPassword()));
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        if(sysUser==null){
            return ResultUtils.error("用户名或密码错误");
        }
        StpUtil.login(sysUser.getUserId(),"PC");
        LoginVo vo=new LoginVo();
        vo.setUserId(sysUser.getUserId());
        vo.setNickName(sysUser.getNickName());
        Map<String,String> map=new HashMap<>();
        map.put("userId",sysUser.getUserId().toString());
//        String token = jwtUtils.createToken(map);
//        vo.setToken(token);
        SaTokenInfo tokenInfo=StpUtil.getTokenInfo();
        vo.setToken(tokenInfo.getTokenValue());
        redisUtils.set(Constant.CACHE_USER_PREFIX+":"+tokenInfo.getTokenValue(), JSON.toJSONString(sysUser));
        return ResultUtils.success("登录成功", vo);
    }



    @PostMapping("/register")
    public ResultVo register(HttpServletRequest request,@RequestBody LoginParam loginParam){

        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUserName,loginParam.getUserName());
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        if(sysUser!=null){
            return ResultUtils.error("用户名已注册");
        }
        sysUser=new SysUser();
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        sysUser.setUserName(loginParam.getUserName());
        sysUser.setNickName(loginParam.getUserName());
        sysUser.setPassword(MD5Utils.encode(loginParam.getPassword()));
        sysUser.setRoleId("7");
        sysUser.setPointNum(100L);
        sysUserService.saveUser(sysUser);
        return ResultUtils.success("注册成功");
    }


    @GetMapping("/getMenusTree")
    public ResultVo getMenusTree(MenuTreeParam menuTreeParam){
        MenuTreeVo menuTreeVo = sysUserService.getMenuTreeVo(menuTreeParam);
        return ResultUtils.success("查询成功",menuTreeVo);
    }


    @PostMapping("/updatePassword")
    public ResultVo updatePassword(@RequestBody UpdatePwdParam updatePwdParam){
        SysUser sysUser = sysUserService.getById(updatePwdParam.getUserId());
        String pwd=MD5Utils.encode(updatePwdParam.getOldPassword());
        if(!pwd.equals(sysUser.getPassword())){
            return ResultUtils.error("原密码错误");
        }

        UpdateWrapper<SysUser> updateWrapper=new UpdateWrapper<>();
        updateWrapper.lambda().set(SysUser::getPassword,MD5Utils.encode(updatePwdParam.getPassword()))
                .eq(SysUser::getUserId,sysUser.getUserId());
        if(sysUserService.update(updateWrapper)){
            return ResultUtils.success("修改成功");
        }
        return ResultUtils.error("修改失败");
    }

    @GetMapping("/getUserInfo")
    //获取用户信息
    public ResultVo getUserInfo(Long userId){
        SysUser sysUser = sysUserService.getById(userId);
        List<SysMenu> menuList=null;
        if("1".equals(sysUser.getIsAdmin())){
            menuList=sysMenuService.list();
        }else{
            menuList=sysMenuService.getMenuByUserId(userId);
        }
        List<String> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> StringUtils.isNotEmpty(item.getCode()))
                .map(item -> item.getCode())
                .collect(Collectors.toList());
        UserInfo info=new UserInfo();
        info.setUserId(userId);
        info.setUserName(sysUser.getNickName());
        info.setPermissions(collect.toArray());
        return ResultUtils.success("查询成功",info);
    }

}
