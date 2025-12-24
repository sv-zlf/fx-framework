package com.fxly.demo.api.core.dto;

import com.fxly.demo.system.global.PageHelper;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zlf
 * @data 2025/12/24
 * @@description
 */

@Data
public class SessionQueryDTO extends PageHelper {

    private String loginLocation;

    private String loginName;

    private String startLoginTime;

    private String endLoginTime;
}
