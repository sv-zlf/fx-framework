package com.fxly.demo.api.core.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.entity.SystemMenu;
import com.fxly.demo.api.core.entity.SystemRoleMenu;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.mapper.SystemRoleMenuMapper;
import com.fxly.demo.api.core.service.ISystemRoleMenuService;
import com.fxly.demo.system.security.SecurityUtils;
import com.fxly.demo.util.tree.TreeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zlf
 * @data 2025/12/1
 * @@description
 */

@Service
public class SystemRoleMenuServiceImpl extends ServiceImpl<SystemRoleMenuMapper, SystemRoleMenu> implements ISystemRoleMenuService {

    @Override
    public List<SystemMenu> toBind(Long roleId) {
        // 获取已授权的菜单信息
        List<SystemRoleMenu> roleMenuList = getRoleMenuList(roleId);
        Set<Long> selectedMenuIds = roleMenuList.stream()
                .map(SystemRoleMenu::getMenuId)
                .collect(Collectors.toSet());
        //
        SystemUser currentLoginUser = SecurityUtils.getCurrentLoginUser();
        Set<Long> currentLoginUserRoleIds = currentLoginUser.getRoleList().stream()
                .map(SystemRole::getId).collect(Collectors.toSet());
        List<SystemMenu> menuList = baseMapper.getMenuListByRoleIds(currentLoginUserRoleIds);

        for (SystemMenu menu : menuList) {
            //  标记是否菜单
            menu.setIsMenu(menu.getType() == 1);
            // 标记是否被目标角色授权
            menu.setIsSelected(selectedMenuIds.contains(menu.getId()));
        }
        return TreeUtils.buildTree(menuList); // 转换为树形结构数据，并返回
    }

    private List<SystemRoleMenu> getRoleMenuList(Long roleId) {
        LambdaQueryWrapper menuLambdaQueryWrapper = new LambdaQueryWrapper<SystemRoleMenu>()
                .eq(SystemRoleMenu::getRoleId, roleId);
        return baseMapper.selectList(menuLambdaQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindMenu(Long roleId, Set<Long> menuIds) {
        // 参数校验
        if (ObjectUtil.isEmpty(roleId)) {
            throw new IllegalArgumentException("角色ID不能为空");
        }

        // 首先，删除角色已绑定的菜单
        LambdaQueryWrapper<SystemRoleMenu> queryWrapper = Wrappers.lambdaQuery(SystemRoleMenu.class)
                .eq(SystemRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);

        // 没有绑定任何菜单的情况
        if(CollectionUtils.isEmpty(menuIds)) {
            return true;
        }

        // 然后，重新绑定
        return baseMapper.bindMenu(roleId, menuIds);
    }
}
