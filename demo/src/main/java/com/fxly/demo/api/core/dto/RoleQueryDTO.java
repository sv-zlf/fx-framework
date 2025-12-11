package com.fxly.demo.api.core.dto;

import com.fxly.demo.system.global.PageHelper;
import lombok.Data;

/**
 * @author zlf
 * @data 2025/12/9
 * @@description
 */

@Data
public class RoleQueryDTO extends PageHelper {

    private String roleName;

    private String roleCode;

    private Integer status;


}
