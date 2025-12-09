package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.entity.SystemRole;
import com.fxly.demo.api.core.entity.SystemUserRole;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author zlf
 * @data 2025/11/28
 * @@description
 */
public interface ISystemUserRoleService extends IService<SystemUserRole> {

    List<SystemRole> getRoleListByUserId(Long userId);

    @Transactional
    boolean grantRole(Long userId, Set<Long> roleIds);

    boolean batchGrant(Set<Long> userIds, Set<Long> roleIds);

}
