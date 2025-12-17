package ${table.basePackage}.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
* ${table.tableComment}
* @author ${table.author}
*/
@Data
public class ${table.className} {
<#list table.columnList as column>
    /**
    * ${column.comment}
    */
    <#if column.autoIncrement>
        private ${column.javaType} ${column.propertyName};
    <#else>
        private ${column.javaType} ${column.propertyName};
    </#if>
</#list>
}