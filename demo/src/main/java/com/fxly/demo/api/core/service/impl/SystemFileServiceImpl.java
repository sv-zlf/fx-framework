package com.fxly.demo.api.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxly.demo.api.core.entity.SystemFile;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.api.core.mapper.SystemFileMapper;
import com.fxly.demo.api.core.service.ISystemFileService;
import com.fxly.demo.utils.FileUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件信息服务实现类
 */
@Slf4j
@Service
public class SystemFileServiceImpl extends ServiceImpl<SystemFileMapper, SystemFile> 
        implements ISystemFileService {

    @Resource
    private FileUtils fileUtils;

    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();
    private static final Map<String, String> FILE_TYPE_MAP = new HashMap<>();

    static {
        // 图片类型
        MIME_TYPE_MAP.put("jpg", "image/jpeg");
        MIME_TYPE_MAP.put("jpeg", "image/jpeg");
        MIME_TYPE_MAP.put("png", "image/png");
        MIME_TYPE_MAP.put("gif", "image/gif");
        MIME_TYPE_MAP.put("bmp", "image/bmp");
        MIME_TYPE_MAP.put("webp", "image/webp");

        // 文档类型
        MIME_TYPE_MAP.put("pdf", "application/pdf");
        MIME_TYPE_MAP.put("doc", "application/msword");
        MIME_TYPE_MAP.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MIME_TYPE_MAP.put("xls", "application/vnd.ms-excel");
        MIME_TYPE_MAP.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_TYPE_MAP.put("ppt", "application/vnd.ms-powerpoint");
        MIME_TYPE_MAP.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        MIME_TYPE_MAP.put("txt", "text/plain");

        // 压缩包类型
        MIME_TYPE_MAP.put("zip", "application/zip");
        MIME_TYPE_MAP.put("rar", "application/vnd.rar");
        MIME_TYPE_MAP.put("7z", "application/x-7z-compressed");

        // 音视频类型
        MIME_TYPE_MAP.put("mp3", "audio/mpeg");
        MIME_TYPE_MAP.put("mp4", "video/mp4");
        MIME_TYPE_MAP.put("avi", "video/x-msvideo");
        MIME_TYPE_MAP.put("mov", "video/quicktime");

        // 文件类型分类
        FILE_TYPE_MAP.put("jpg", "image");
        FILE_TYPE_MAP.put("jpeg", "image");
        FILE_TYPE_MAP.put("png", "image");
        FILE_TYPE_MAP.put("gif", "image");
        FILE_TYPE_MAP.put("bmp", "image");
        FILE_TYPE_MAP.put("webp", "image");
        FILE_TYPE_MAP.put("pdf", "document");
        FILE_TYPE_MAP.put("doc", "document");
        FILE_TYPE_MAP.put("docx", "document");
        FILE_TYPE_MAP.put("xls", "document");
        FILE_TYPE_MAP.put("xlsx", "document");
        FILE_TYPE_MAP.put("ppt", "document");
        FILE_TYPE_MAP.put("pptx", "document");
        FILE_TYPE_MAP.put("txt", "document");
        FILE_TYPE_MAP.put("zip", "archive");
        FILE_TYPE_MAP.put("rar", "archive");
        FILE_TYPE_MAP.put("7z", "archive");
        FILE_TYPE_MAP.put("mp3", "audio");
        FILE_TYPE_MAP.put("mp4", "video");
        FILE_TYPE_MAP.put("avi", "video");
        FILE_TYPE_MAP.put("mov", "video");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemFile uploadFile(MultipartFile file, String subPath, SystemUser user) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String relativePath = fileUtils.uploadFile(file, subPath);
        
        SystemFile fileInfo = new SystemFile();
        fileInfo.setOriginalName(originalFilename);
        fileInfo.setFilePath(relativePath);
        fileInfo.setFileSize(file.getSize());
        
        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);
        fileInfo.setFileExtension(extension);
        
        // 设置文件类型和MIME类型
        fileInfo.setMimeType(MIME_TYPE_MAP.getOrDefault(extension, "application/octet-stream"));
        fileInfo.setFileType(FILE_TYPE_MAP.getOrDefault(extension, "other"));
        
        // 计算MD5
        String md5 = calculateMD5(file.getBytes());
        fileInfo.setMd5(md5);
        
        // 设置用户信息
        if (user != null) {
            fileInfo.setUserId(user.getId());
            fileInfo.setUsername(user.getUserName());
        }
        
        this.save(fileInfo);
        
        log.info("文件上传成功: {}, ID: {}", originalFilename, fileInfo.getId());
        
        return fileInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long id) {
        SystemFile fileInfo = this.getById(id);
        if (fileInfo == null) {
            log.warn("文件不存在，ID: {}", id);
            return false;
        }
        
        // 删除物理文件
        boolean deleted = fileUtils.deleteFile(fileInfo.getFilePath());
        
        // 删除数据库记录（逻辑删除）
        boolean dbDeleted = this.removeById(id);
        
        if (deleted && dbDeleted) {
            log.info("文件删除成功: {}, ID: {}", fileInfo.getOriginalName(), id);
            return true;
        } else {
            log.warn("文件删除部分失败: {}, 物理删除: {}, 数据库删除: {}", 
                fileInfo.getOriginalName(), deleted, dbDeleted);
            return false;
        }
    }

    @Override
    public File getFile(String relativePath) {
        String absolutePath = fileUtils.getAbsolutePath(relativePath);
        if (absolutePath == null) {
            return null;
        }
        return new File(absolutePath);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 计算MD5
     */
    private String calculateMD5(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
