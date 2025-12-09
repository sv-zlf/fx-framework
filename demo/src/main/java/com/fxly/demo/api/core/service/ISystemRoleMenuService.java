package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.entity.SystemMenu;
import com.fxly.demo.api.core.entity.SystemRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * @author zlf
 * @data 2025/12/1
 * @@description
 */
public interface ISystemRoleMenuService extends IService<SystemRoleMenu> {

    List<SystemMenu> toBind(Long roleId);

    boolean bindMenu(Long roleId, Set<Long> menuIds);

}
