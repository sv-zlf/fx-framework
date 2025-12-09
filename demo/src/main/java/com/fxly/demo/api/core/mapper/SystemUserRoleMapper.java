package com.fxly.demo.api.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.entity.SystemUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */
public interface SystemUserRoleMapper extends BaseMapper<SystemUserRole> {

    List<SystemRole> getRoleListByUserId(@Param("userId") Long userId);

    boolean grantRole(@Param("userId") Long userId, @Param("roleIds") Set<Long> roleIds);

    boolean deleteBatch(@Param("userId") Long userId, @Param("roleIdList") List<Long> roleIdList);
}
