package com.fxly.demo.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.fxly.demo.api.generate.entity.ColumnInfo;
import com.fxly.demo.api.generate.entity.TableInfo;
import com.fxly.demo.system.global.GlobalException;
import com.fxly.demo.system.security.SecurityUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SQL解析工具类（基于Alibaba Druid）
 * @author zlf
 * @date 2025/12/16
 */

public class SqlParserUtil {

    /**
     * 数据库类型：默认MySQL（可扩展为Oracle/PostgreSQL等）
     */
    private static final String DB_TYPE = JdbcConstants.MYSQL.toString();

    /**
     * 正则表达式：匹配表注释的COMMENT子句（如 COMMENT='用户信息表'、COMMENT "用户信息表"）
     */
    private static final Pattern TABLE_COMMENT_PATTERN = Pattern.compile("COMMENT\\s*=?\\s*['\"`]?(.*?)['\"`]?\\s*(,|\\))", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);


    /**
     * 解析SQL语句（支持单个/多个CREATE TABLE语句）
     *
     * @param sql SQL语句（可包含多个CREATE TABLE语句，以;分隔）
     * @return 表信息列表
     * @throws IllegalArgumentException 解析失败时抛出异常
     */
    public static List<TableInfo> parseSqls(String sql, String moudleName) {

        try {
            // 参数校验
            if (StringUtils.isBlank(sql)) {
                throw new IllegalArgumentException("SQL语句不能为空");
            }

            // 1. 解析SQL，获取所有SQL语句节点
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DB_TYPE);
            if (statementList.isEmpty()) {
                throw new IllegalArgumentException("SQL语句解析失败，未找到有效语句");
            }

            List<TableInfo> tableInfoList = new ArrayList<>();
            // 2. 遍历所有语句节点，筛选出CREATE TABLE语句并逐个解析
            for (SQLStatement statement : statementList) {
                if (statement instanceof SQLCreateTableStatement) {
                    SQLCreateTableStatement createTableStatement = (SQLCreateTableStatement) statement;
                    // 解析单个建表语句，封装为TableInfo
                    TableInfo tableInfo = parseSingleCreateTable(createTableStatement, sql);
                    tableInfo.setModuleName(moudleName);
                    tableInfo.setAuthor(SecurityUtils.getUserName());
                    tableInfoList.add(tableInfo);
                }
            }

            if (tableInfoList.isEmpty()) {
                throw new IllegalArgumentException("SQL中未包含有效的CREATE TABLE语句");
            }

            return tableInfoList;
        }
        catch (Exception e) {
            throw new GlobalException(500,"SQL语句解析失败，请输入正确SQL");
        }
    }

    /**
     * 解析SQL语句，提取表和列信息
     *
     * @param sql SQL语句（主要是CREATE TABLE语句）
     * @return TableInfo对象
     * @throws IllegalArgumentException 解析失败时抛出异常
     */
    /**
     * 解析单个CREATE TABLE语句，封装为TableInfo
     */
    private static TableInfo parseSingleCreateTable(SQLCreateTableStatement createTableStatement, String originalSql) {
        // 3. 封装表基本信息
        TableInfo tableInfo = new TableInfo();
        // 表名（去除反引号，兼容MySQL）
        String tableName = createTableStatement.getTableName().replace("`", "");
        tableInfo.setTableName(tableName);
        // 类名：下划线转驼峰（首字母大写）
        tableInfo.setClassName(underlineToCamelUpper(tableName));
        // 表注释（增强解析，支持多场景）
        tableInfo.setTableComment(getTableComment(createTableStatement, originalSql));

        // 4. 获取主键列名集合（表级主键，纯字符串解析，兼容所有版本）
        Set<String> primaryKeyColumns = getPrimaryKeyColumns(createTableStatement);

        // 5. 封装列信息
        List<ColumnInfo> columnList = new ArrayList<>();
        for (SQLColumnDefinition columnDef : createTableStatement.getColumnDefinitions()) {
            ColumnInfo columnInfo = new ColumnInfo();

            // 列名（去除反引号）
            String columnName = columnDef.getName().getSimpleName().replace("`", "");
            columnInfo.setColumnName(columnName);

            // 属性名：下划线转驼峰（首字母小写）
            columnInfo.setPropertyName(underlineToCamel(columnName));

            // 列类型（含长度，如VARCHAR(255)）
            String columnType = buildColumnType(columnDef);
            columnInfo.setColumnType(columnType);

            // Java类型（数据库类型映射）
            columnInfo.setJavaType(getColumnJavaType(columnType.toUpperCase()));

            // 列注释
            columnInfo.setComment(columnDef.getComment().toString());

            // 是否主键（列级+表级）
            columnInfo.setPrimaryKey(primaryKeyColumns.contains(columnName) || isColumnPrimaryKey(columnDef));

            // 是否非空
            columnInfo.setNotNull(isColumnNotNull(columnDef));

            // 是否自增（MySQL: AUTO_INCREMENT）
            columnInfo.setAutoIncrement(isColumnAutoIncrement(columnDef));

            columnList.add(columnInfo);
        }
        tableInfo.setColumnList(columnList);

        return tableInfo;
    }


    /**
     * 构建列类型（含长度/精度，如VARCHAR(255)、DECIMAL(10,2)）
     */
    private static String buildColumnType(SQLColumnDefinition columnDef) {
        StringBuilder columnType = new StringBuilder(columnDef.getDataType().getName().toUpperCase());
        // 拼接长度/精度参数
        List<SQLExpr> arguments = columnDef.getDataType().getArguments();
        if (arguments != null && !arguments.isEmpty()) {
            String args = arguments.stream()
                    .map(expr -> expr.toString().replace("`", ""))
                    .collect(Collectors.joining(",", "(", ")"));
            columnType.append(args);
        }
        return columnType.toString();
    }

    /**
     * 获取表注释（增强版：支持Druid节点解析+正则匹配原始SQL，解决注释识别问题）
     */
    private static String getTableComment(SQLCreateTableStatement createTableStatement, String originalSql) {
        // 场景1：处理MySQL专属的注释属性（MySqlCreateTableStatement）
        if (createTableStatement instanceof MySqlCreateTableStatement) {
            SQLExpr commentExpr = ((MySqlCreateTableStatement) createTableStatement).getComment();
            String comment = getExprValue(commentExpr);
            if (StringUtils.isNotBlank(comment)) {
                return cleanComment(comment);
            }
        }

        // 场景2：遍历表选项中的COMMENT
        for (SQLAssignItem item : createTableStatement.getTableOptions()) {
            String target = item.getTarget().toString().toUpperCase();
            if ("COMMENT".equals(target)) {
                String comment = getExprValue(item.getValue());
                if (StringUtils.isNotBlank(comment)) {
                    return cleanComment(comment);
                }
            }
        }

        String tableName = createTableStatement.getTableName().replace("`", "");
        Pattern tablePattern = Pattern.compile("CREATE TABLE\\s+[`\"]?" + tableName + "[`\"]?\\s*\\([\\s\\S]*?\\)\\s*[^;]*", Pattern.CASE_INSENSITIVE);
        Matcher tableMatcher = tablePattern.matcher(originalSql);
        String tableSql = "";
        if (tableMatcher.find()) {
            tableSql = tableMatcher.group();
        }

        Matcher matcher = TABLE_COMMENT_PATTERN.matcher(tableSql);
        if (matcher.find()) {
            String comment = matcher.group(1);
            if (StringUtils.isNotBlank(comment)) {
                return cleanComment(comment);
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * 提取SQL表达式的值（支持SQLCharExpr、String等类型）
     */
    private static String getExprValue(Object expr) {
        if (expr == null) {
            return StringUtils.EMPTY;
        }
        // 处理SQLCharExpr类型（Druid解析字符串常量的节点）
        if (expr instanceof SQLCharExpr) {
            return (String) ((SQLCharExpr) expr).getValue();
        }
        // 处理普通字符串或其他类型
        return expr.toString();
    }

    /**
     * 获取表级主键列名集合（纯字符串解析，兼容所有Druid版本）
     */
    private static Set<String> getPrimaryKeyColumns(SQLCreateTableStatement createTableStatement) {
        Set<String> primaryKeyColumns = new HashSet<>();

        // 遍历表元素列表，查找主键约束（纯字符串解析，不依赖任何枚举/子类）
        for (SQLTableElement tableElement : createTableStatement.getTableElementList()) {
            String elementStr = tableElement.toString().toUpperCase().replaceAll("\\s+", " "); // 统一空格为单个空格
            // 判断是否包含主键约束关键字
            if (elementStr.contains("PRIMARY KEY")) {
                // 提取括号内的列名（如 PRIMARY KEY (id, name) → id, name）
                int start = elementStr.indexOf("(") + 1;
                int end = elementStr.lastIndexOf(")");
                if (start > 0 && end > start) {
                    String columnsStr = elementStr.substring(start, end);
                    // 分割列名（处理多个主键列的情况）
                    String[] columnArray = columnsStr.split(",");
                    for (String col : columnArray) {
                        String columnName = col.trim().replace("`", "").replace(" ", "");
                        if (StringUtils.isNotBlank(columnName)) {
                            primaryKeyColumns.add(columnName);
                        }
                    }
                }
            }
        }

        return primaryKeyColumns;
    }

    /**
     * 判断列是否是列级主键（列定义中的PRIMARY KEY，纯字符串解析）
     */
    private static boolean isColumnPrimaryKey(SQLColumnDefinition columnDef) {
        // 1. 遍历列约束，通过字符串判断是否是主键
        for (SQLConstraint constraint : columnDef.getConstraints()) {
            if (constraint.toString().toUpperCase().replaceAll("\\s+", " ").contains("PRIMARY KEY")) {
                return true;
            }
        }

        // 2. 兜底：遍历列属性（部分版本中主键约束在attributes中）
        Map<String, Object> attributes = columnDef.getAttributes();
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                String keyUpper = key.toUpperCase().replaceAll("\\s+", " ");
                if (keyUpper.contains("PRIMARY") && keyUpper.contains("KEY")) {
                    return true;
                }
            }
        }

        // 3. 最终兜底：列定义的完整字符串判断
        return columnDef.toString().toUpperCase().replaceAll("\\s+", " ").contains("PRIMARY KEY");
    }

    /**
     * 判断列是否非空（NOT NULL，纯字符串解析）
     */
    private static boolean isColumnNotNull(SQLColumnDefinition columnDef) {
        // 1. 遍历列约束，通过字符串判断是否非空
        for (SQLConstraint constraint : columnDef.getConstraints()) {
            String constraintStr = constraint.toString().toUpperCase().replaceAll("\\s+", " ");
            if (constraintStr.equals("NOT NULL") || constraintStr.contains("NOT NULL")) {
                return true;
            }
        }

        // 2. 兜底：遍历列属性
        Map<String, Object> attributes = columnDef.getAttributes();
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                if (key.toUpperCase().replaceAll("\\s+", " ").equals("NOT NULL")) {
                    return true;
                }
            }
        }

        // 3. 最终兜底：列定义的完整字符串判断
        return columnDef.toString().toUpperCase().replaceAll("\\s+", " ").contains("NOT NULL");
    }

    /**
     * 判断列是否自增（MySQL: AUTO_INCREMENT，纯字符串解析）
     */
    private static boolean isColumnAutoIncrement(SQLColumnDefinition columnDef) {
        // 1. 遍历列属性Map
        Map<String, Object> attributes = columnDef.getAttributes();
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                if (key.toUpperCase().replaceAll("\\s+", " ").equals("AUTO_INCREMENT")) {
                    return true;
                }
            }
        }

        // 2. 最终兜底：列定义的完整字符串判断（最可靠的方式）
        return columnDef.toString().toUpperCase().replaceAll("\\s+", " ").contains("AUTO_INCREMENT");
    }

    /**
     * 清理注释内容（去除单引号、双引号、反引号、空格）
     */
    private static String cleanComment(String comment) {
        if (StringUtils.isBlank(comment)) {
            return StringUtils.EMPTY;
        }
        // 去除各种引号和前后空格，保留注释核心内容
        return comment.replace("'", "").replace("\"", "").replace("`", "").trim();
    }

    /**
     * 数据库列类型映射为Java类型（可根据需求扩展）
     */
    /**
     * 数据库列类型转Java类型名（大写开头，如String、Integer、Long）
     */
    private static String getColumnJavaType(String columnType) {
        // 统一转大写（避免数据库类型小写导致匹配失败，增强鲁棒性）
        String upperColumnType = columnType.toUpperCase();

        if (upperColumnType.startsWith("VARCHAR") || upperColumnType.startsWith("CHAR") ||
                upperColumnType.startsWith("TEXT") || upperColumnType.startsWith("LONGTEXT") ||
                upperColumnType.startsWith("MEDIUMTEXT") || upperColumnType.startsWith("TINYTEXT")) {
            return "String"; // 字符串类型→String
        } else if (upperColumnType.startsWith("INT") || upperColumnType.startsWith("TINYINT") ||
                upperColumnType.startsWith("SMALLINT") || upperColumnType.startsWith("MEDIUMINT")) {
            return "Integer"; // 整型→Integer（Java常用包装类）
        } else if (upperColumnType.startsWith("BIGINT")) {
            return "Long"; // 长整型→Long
        } else if (upperColumnType.startsWith("FLOAT")) {
            return "Float"; // 浮点型→Float
        } else if (upperColumnType.startsWith("DOUBLE")) {
            return "Double"; // 双精度浮点型→Double
        } else if (upperColumnType.startsWith("DECIMAL") || upperColumnType.startsWith("NUMERIC")) {
            return "BigDecimal"; // 高精度小数→BigDecimal
        } else if (upperColumnType.startsWith("DATE")) {
            return "LocalDate"; // 日期→LocalDate
        } else if (upperColumnType.startsWith("DATETIME") || upperColumnType.startsWith("TIMESTAMP")) {
            return "LocalDateTime";
        } else if (upperColumnType.startsWith("BOOLEAN")) {
            return "Boolean"; // 布尔型→Boolean
        } else {
            // 默认映射为String
            return "String";
        }
    }

    /**
     * 下划线转驼峰（首字母小写）：如user_name → userName
     */
    public static String underlineToCamel(String str) {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }
        str = str.toLowerCase();
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                sb.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰（首字母大写）：如user_name → UserName
     */
    public static String underlineToCamelUpper(String str) {
        String camel = underlineToCamel(str);
        if (StringUtils.isBlank(camel)) {
            return StringUtils.EMPTY;
        }
        return Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
    }
}