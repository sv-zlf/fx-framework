package com.fxly.demo.api.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fxly.demo.api.core.entity.SystemUser;
import org.apache.ibatis.annotations.Param;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */

public interface SystemUserMapper extends BaseMapper<SystemUser> {

    /**
     * 根据用户名查询用户（关联角色+权限）
     */
    SystemUser selectUserWithRolesAndPermissions(@Param("userName") String userName);
}
