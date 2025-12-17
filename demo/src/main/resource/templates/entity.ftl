package ${table.basePackage}.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
* ${table.tableComment}
* @author ${table.author}
*/

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class ${table.className} implements Serializable{

    private static final long serialVersionUID = 1L;

<#list table.columnList as column>
    @Schema(description = "${column.comment}")
    <#if column.autoIncrement>
    @TableId(type = IdType.AUTO)
    private ${column.javaType} ${column.propertyName};
    <#else>
    private ${column.javaType} ${column.propertyName};
    </#if>

</#list>

}