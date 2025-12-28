package com.fxly.demo.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 */
@Slf4j
@Component
public class FileUtils {

    @Value("${spring.file.upload-path:./uploads}")
    private String uploadPath;

    @Value("${spring.file.allowed-extensions:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,txt,zip,rar}")
    private String allowedExtensions;

    @Value("${spring.file.max-file-size:104857600}")
    private long maxFileSize;

    private Path uploadDirPath;

    @PostConstruct
    public void init() {
        // 处理相对路径，确保使用项目根目录
        uploadDirPath = Paths.get(uploadPath).normalize().toAbsolutePath();
        log.info("文件上传根目录: {}", uploadDirPath.toAbsolutePath());
        
        // 创建根目录
        if (!Files.exists(uploadDirPath)) {
            try {
                Files.createDirectories(uploadDirPath);
                log.info("创建文件上传根目录: {}", uploadDirPath.toAbsolutePath());
            } catch (IOException e) {
                log.error("创建文件上传目录失败", e);
            }
        }
    }

    /**
     * 上传文件
     * @param file 文件
     * @param subPath 子目录（可选）
     * @return 文件路径
     */
    public String uploadFile(MultipartFile file, String subPath) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedExtension(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("文件大小超过限制: " + (maxFileSize / 1024 / 1024) + "MB");
        }

        String datePath = getDateString();
        String newFilename = generateFilename(fileExtension);
        
        // 构建相对路径
        String relativePath = (subPath != null && !subPath.isEmpty()) 
            ? datePath + "/" + subPath + "/" + newFilename
            : datePath + "/" + newFilename;
        
        // 构建完整目录路径
        Path dateDir = uploadDirPath.resolve(datePath);
        if (subPath != null && !subPath.isEmpty()) {
            dateDir = dateDir.resolve(subPath);
        }
        
        // 创建所有必要的目录
        if (!Files.exists(dateDir)) {
            Files.createDirectories(dateDir);
            log.info("创建上传目录: {}", dateDir.toAbsolutePath());
        }
        
        // 目标文件路径
        Path filePath = dateDir.resolve(newFilename);
        
        // 保存文件
        file.transferTo(filePath.toFile());
        
        log.info("文件上传成功: {} -> {}", originalFilename, filePath.toAbsolutePath());
        
        return relativePath;
    }

    /**
     * 上传文件（无子目录）
     */
    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, null);
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }
        
        Path filePath = uploadDirPath.resolve(relativePath.replace("/", File.separator));
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功: {}", filePath.toAbsolutePath());
                return true;
            } else {
                log.warn("文件不存在: {}", filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("文件删除失败: {}", filePath.toAbsolutePath(), e);
        }
        return false;
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }
        Path filePath = uploadDirPath.resolve(relativePath.replace("/", File.separator));
        return Files.exists(filePath);
    }

    /**
     * 获取文件绝对路径
     */
    public String getAbsolutePath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        Path filePath = uploadDirPath.resolve(relativePath.replace("/", File.separator));
        return filePath.toAbsolutePath().toString();
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 检查扩展名是否允许
     */
    private boolean isAllowedExtension(String extension) {
        List<String> allowedList = Arrays.asList(allowedExtensions.split(","));
        return allowedList.contains(extension.toLowerCase());
    }

    /**
     * 生成唯一文件名
     */
    private String generateFilename(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * 获取日期字符串用于目录
     */
    private String getDateString() {
        return java.time.LocalDate.now().toString().replace("-", "");
    }

}
