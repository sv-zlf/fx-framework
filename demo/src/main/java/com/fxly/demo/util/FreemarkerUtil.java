package com.fxly.demo.util;

import com.fxly.demo.api.generate.entity.TableInfo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Freemarker模板工具类
 */
public class FreemarkerUtil {
    private static final Configuration CONFIG;

    private static final String OUTPUT_DIR = "src/main/java/";

    private static final String BASE_PACKAGE = "com/fxly/demo/api/";

    static {
        // 初始化Freemarker配置
        CONFIG = new Configuration(Configuration.VERSION_2_3_32);
        // 模板文件路径：resources/templates（可自定义）
        CONFIG.setClassForTemplateLoading(FreemarkerUtil.class, "/templates");
        CONFIG.setDefaultEncoding("UTF-8");
    }

    /**
     * 生成代码（支持多表）
     */
    public static void generateCodeBatch(List<TableInfo> tableInfoList) {
        for (TableInfo tableInfo : tableInfoList) {
            generateCode(tableInfo);
        }
    }

    public static void generateCodeBatch(List<TableInfo> tableInfoList, ZipOutputStream zipOut) {
        for (TableInfo tableInfo : tableInfoList) {
            // 传入ZIP流，处理ZIP生成逻辑
            generateCode(tableInfo, zipOut);
        }
    }

    public static void generateCode(TableInfo tableInfo, ZipOutputStream zipOut) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("table", tableInfo);

        String moduleName = tableInfo.getMoudleName();
        tableInfo.setBasePackage(BASE_PACKAGE.replaceAll("/", ".") + moduleName);
        String className = tableInfo.getClassName();

        // 构建路径（原有逻辑复用）
        String entityPath = OUTPUT_DIR + BASE_PACKAGE + moduleName + "/entity/" + className + ".java";
        String mapperPath = OUTPUT_DIR + BASE_PACKAGE + moduleName + "/mapper/" + className + "Mapper.java";
        String servicePath = OUTPUT_DIR + BASE_PACKAGE + moduleName + "/service/" + "I" + className + "Service.java";
        String serviceImplPath = OUTPUT_DIR + BASE_PACKAGE + moduleName + "/service/impl/" + className + "ServiceImpl.java";
        String controllerPath = OUTPUT_DIR + BASE_PACKAGE + moduleName + "/controller/" + className + "Controller.java";

        generateFileToZip("entity.ftl", dataModel, entityPath, zipOut);
        generateFileToZip("mapper.ftl", dataModel, mapperPath, zipOut);
        generateFileToZip("service.ftl", dataModel, servicePath, zipOut);
        generateFileToZip("serviceImpl.ftl", dataModel, serviceImplPath, zipOut);
        generateFileToZip("controller.ftl", dataModel, controllerPath, zipOut);
    }


    /**
     * 生成单表的Entity/Mapper/Service/Controller
     */
    public static void generateCode(TableInfo tableInfo) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("table", tableInfo);

        String moduleName = tableInfo.getMoudleName();
        tableInfo.setBasePackage(BASE_PACKAGE.replaceAll("/", ".")+moduleName);
        String className = tableInfo.getClassName();

        // 1. 生成实体类
        generateFile("entity.ftl", dataModel, OUTPUT_DIR + BASE_PACKAGE + moduleName+ "/entity/" + className + ".java");
        // 2. 生成Mapper接口
        generateFile("mapper.ftl", dataModel, OUTPUT_DIR + BASE_PACKAGE + moduleName+ "/mapper/" + className + "Mapper.java");
        // 3. 生成Service接口
        generateFile("service.ftl", dataModel, OUTPUT_DIR + BASE_PACKAGE + moduleName +"/service/" + "I"+ className + "Service.java");
        // 4. 生成Service实现类
        generateFile("serviceImpl.ftl", dataModel, OUTPUT_DIR + BASE_PACKAGE + moduleName+"/service/impl/" + className + "ServiceImpl.java");
        // 5. 生成Controller
        generateFile("controller.ftl", dataModel, OUTPUT_DIR + BASE_PACKAGE + moduleName+ "/controller/" + className + "Controller.java");
    }

    /**
     * 生成文件 - 压缩包
     * @param templateName 模板文件名
     * @param dataModel 数据模型
     * @param outputPath 输出文件路径
     */
    private static void generateFileToZip(String templateName, Map<String, Object> dataModel, String outputPath, ZipOutputStream zipOut) {
        try {
            Template template = CONFIG.getTemplate(templateName);
            // 移除本地输出目录前缀（ZIP内路径更简洁）
            String zipEntryPath = outputPath.replace(OUTPUT_DIR, "");
            ZipEntry zipEntry = new ZipEntry(zipEntryPath);
            zipOut.putNextEntry(zipEntry);

            // ========== 关键修复：移除try-with-resources，不自动关闭Writer ==========
            Writer writer = new OutputStreamWriter(zipOut, "UTF-8");
            template.process(dataModel, writer);
            writer.flush(); // 仅刷新数据，不关闭流
            // 不要调用writer.close()，否则会关闭底层的zipOut

            zipOut.closeEntry();
            System.out.println("ZIP文件生成成功：" + zipEntryPath);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("生成ZIP文件失败：" + e.getMessage(), e);
        }
    }

    /**
     * 生成文件
     * @param templateName 模板文件名
     * @param dataModel 数据模型
     * @param outputPath 输出文件路径
     */
    public static void generateFile(String templateName, Map<String, Object> dataModel, String outputPath) {
        try {
            // 加载模板
            Template template = CONFIG.getTemplate(templateName);
            // 创建输出目录
            File outputFile = new File(outputPath);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            // 生成文件
            try (Writer writer = new FileWriter(outputFile, false)) {
                template.process(dataModel, writer);
            }
            System.out.println("文件生成成功：" + outputPath);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("生成文件失败：" + e.getMessage(), e);
        }
    }
}