package com.fxly.demo.api.core.dto;

import com.fxly.demo.system.global.PageHelper;
import lombok.Data;

import java.util.Set;

/**
 * @author zlf
 * @data 2025/12/4
 * @@description
 */

@Data
public class MenuQueryDTO extends PageHelper {

    // 当前登录用户角色编号
    Set<Long> currentLoginUserRoleIds;
    // 菜单名称
    private String menuName;
    // 状态
    private Integer status;
    // 是否隐藏
    private Integer hide;
}
