package com.fxly.demo.api.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fxly.demo.api.core.entity.SystemMenu;
import com.fxly.demo.api.core.entity.SystemRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author zlf
 * @data 2025/12/1
 * @@description
 */
public interface SystemRoleMenuMapper extends BaseMapper<SystemRoleMenu> {

    List<SystemMenu> getMenuListByRoleIds(@Param("roleIds") Set<Long> roleIds);

    boolean bindMenu(@Param("roleId") Long roleId, @Param("menuIds") Set<Long> menuIds);

}
