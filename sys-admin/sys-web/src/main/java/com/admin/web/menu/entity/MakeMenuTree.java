package com.admin.web.menu.entity;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MakeMenuTree {
    public static List<SysMenu> makeTree(List<SysMenu> menus,Long pid) {
        List<SysMenu> list = new ArrayList<SysMenu>();
        Optional.ofNullable(menus).orElse(new ArrayList<>())
                .stream()
                .filter(item->item!=null&&item.getParentId().equals(pid))
                .forEach(item->{
                    SysMenu menu = new SysMenu();
                    BeanUtils.copyProperties(item,menu);
                    menu.setLabel(item.getTitle());
                    menu.setValue(item.getMenuId());
                    menu.setVShow(item.getVShow());
                    //查找下级 递归
                    List<SysMenu> children = makeTree(menus, item.getMenuId());
                    menu.setChildren(children);
                    list.add(menu);
                });
        return list;

    }

    public static List<RouterVo> makeRouter(List<SysMenu> menus,Long pid) {
        List<RouterVo> list = new ArrayList<>();
        Optional.ofNullable(menus).orElse(new ArrayList<>())
                .stream()
                .filter(item->item!=null&&item.getParentId().equals(pid))
                .forEach(item->{
                    RouterVo vo = new RouterVo();
                    vo.setName(item.getName());
                    vo.setPath(item.getPath());
                    vo.setVShow(item.getVShow());
                    List<RouterVo> children=makeRouter(menus,item.getMenuId());
                    vo.setChildren(children);
                    if(item.getParentId()==0L){
                        vo.setComponent("Layout");
                        if(item.getType().equals("1")){
                            vo.setRedirect(item.getPath());
                            //菜单需要设置children
                            List<RouterVo> listChildren=new ArrayList<>();
                            RouterVo routerVo=new RouterVo();
                            routerVo.setName(item.getName());
                            routerVo.setPath(item.getPath());
                            routerVo.setComponent(item.getUrl());
                            routerVo.setVShow(item.getVShow());
                            routerVo.setMeta(routerVo.new Meta(
                                    item.getTitle(),
                                    item.getIcon(),
                                    item.getCode().split(",")
                            ));
                            listChildren.add(routerVo);
                            vo.setChildren(listChildren);
                            vo.setPath(item.getPath()+"parent");//扩展字段
                            vo.setName(item.getName()+"parent");//扩展字段
                        }
                    }else{
                        vo.setComponent(item.getUrl());
                    }
                    vo.setMeta(vo.new Meta(
                            item.getTitle(),
                            item.getIcon(),
                            item.getCode().split(",")
                    ));
                    list.add(vo);
                });
            return list;
    }
}
