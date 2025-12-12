package com.fxly.demo.api.core.dto;

import com.fxly.demo.system.global.PageHelper;
import lombok.Data;

/**
 * @author zlf
 * @data 2025/11/30
 * @@description
 */
@Data
public class UserQueryDTO extends PageHelper {

    // 用户名
    private String name;

    private String phone;

    private Integer status;

}