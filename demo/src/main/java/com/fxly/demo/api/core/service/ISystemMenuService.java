package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.MenuQueryDTO;
import com.fxly.demo.api.core.entity.SystemMenu;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */
public interface ISystemMenuService extends IService<SystemMenu> {

    Page<SystemMenu> getPageList(MenuQueryDTO menuQuery);

    List<SystemMenu> getMenuList(MenuQueryDTO menuQuery);

    List<SystemMenu> getMenuTree(MenuQueryDTO menuQuery);

    @Transactional(rollbackFor = Exception.class)
    boolean insert(SystemMenu menu);

    @Transactional(rollbackFor = Exception.class)
    boolean delete(Long menuId);

}
