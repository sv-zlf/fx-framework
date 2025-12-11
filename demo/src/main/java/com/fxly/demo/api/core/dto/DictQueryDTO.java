package com.fxly.demo.api.core.dto;

import com.fxly.demo.system.global.PageHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zlf
 * @data 2025/12/4
 * @@description
 */

@Data
public class DictQueryDTO extends PageHelper {

    @Schema(description = "字典项编码")
    private String dictItemCode;

    @Schema(description = "字典项名称")
    private String dictItemName;

    @Schema(description = "字典类型编码")
    private String dictTypeCode;

    @Schema(description = "字典类型名称")
    private String dictTypeName;

    @Schema(description = "字典类型状态")
    private Integer status;
}
