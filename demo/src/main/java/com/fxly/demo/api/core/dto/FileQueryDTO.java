package com.fxly.demo.api.core.dto;

import com.fxly.demo.system.global.PageHelper;
import lombok.Data;

/**
 * @author zlf
 * @data 2025/12/31
 */

@Data
public class FileQueryDTO extends PageHelper {
    private String fileName;
    private String fileType;

}
