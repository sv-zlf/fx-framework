package com.fxly.demo.api.core.dto;

import com.fxly.demo.system.global.PageHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author zlf
 * @data 2025/12/4
 * @@description
 */

@Data
public class SystemMenuQueryDTO extends PageHelper {
    Set<Long> currentLoginUserRoleIds;
    private Long pid;
    private String menuName;
    private Integer level;
    private Boolean isUsed;
}
