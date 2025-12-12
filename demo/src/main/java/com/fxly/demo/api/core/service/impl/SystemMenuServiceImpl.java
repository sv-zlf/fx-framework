package com.fxly.demo.api.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.dto.MenuQueryDTO;
import com.fxly.demo.api.core.entity.SystemMenu;
import com.fxly.demo.api.core.entity.SystemRoleMenu;
import com.fxly.demo.api.core.mapper.SystemMenuMapper;
import com.fxly.demo.api.core.service.ISystemMenuService;
import com.fxly.demo.api.core.service.ISystemRoleMenuService;
import com.fxly.demo.system.global.GlobalException;
import com.fxly.demo.system.security.SecurityUtils;
import com.fxly.demo.util.tree.TreeUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */

@Service
@Slf4j
public class SystemMenuServiceImpl extends ServiceImpl<SystemMenuMapper, SystemMenu> implements ISystemMenuService {

    @Resource
    private ISystemRoleMenuService roleMenuService;

    @Override
    public List<SystemMenu> getMenuList(MenuQueryDTO menuQuery) {
        menuQuery.setCurrentLoginUserRoleIds(SecurityUtils.getRoleIds());
        return baseMapper.getMenuList(menuQuery);
    }

    @Override
    public List<SystemMenu> getMenuTree(MenuQueryDTO menuQuery) {
        List<SystemMenu> menuList = baseMapper.getMenuList(menuQuery);
        return TreeUtils.buildTree(menuList);
    }

    @Override
    public boolean insert(SystemMenu menu) {
        menu.setStatus(1); // 状态默认为启用
        // 检查父级菜单
        if (menu.getParentId()!=null){
            SystemMenu parentMenu = getById(menu.getParentId());
            // 类型为目录或者菜单只能挂载在目录下
            if (menu.getType()==1 || menu.getType()==2){
                if (parentMenu.getType()!=1){
                    throw new GlobalException(500, "目录或菜单只能作为目录的子菜单");
                }
            }
        }
        // 保存菜单
        boolean b = save(menu);
        if(b) {
            saveRoleMenu(menu); // 把当前新建菜单与当前系统用户角色建立关联关系
        }
        return b;
    }

    @Override
    public boolean delete(Long menuId) {
        boolean b = removeById(menuId);
        if (b) {
            // 删除已绑定的角色
            removeRoleMenu(menuId);
        }
        return b;
    }

    private void saveRoleMenu(SystemMenu menu) {
        List<SystemRoleMenu> roleMenuList = new ArrayList<>();
        Set<Long> roleIds = SecurityUtils.getRoleIds();
        roleIds.forEach(roleId -> {
            roleMenuList.add(new SystemRoleMenu().setRoleId(roleId).setMenuId(menu.getId()));
        });
        roleMenuService.saveBatch(roleMenuList);
    }

    private void removeRoleMenu(Long menuId) {
        LambdaQueryWrapper roleMenuLambdaQueryWrapper = new LambdaQueryWrapper<SystemRoleMenu>()
                .eq(SystemRoleMenu::getMenuId, menuId);
        Long count = roleMenuService.count(roleMenuLambdaQueryWrapper);
        if (count > 0) {
            roleMenuService.remove(roleMenuLambdaQueryWrapper);
        }
    }

}
