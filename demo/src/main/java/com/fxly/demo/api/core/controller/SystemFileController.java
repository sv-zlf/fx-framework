package com.fxly.demo.api.core.controller;

import com.fxly.demo.api.core.entity.SystemFile;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.api.core.service.ISystemFileService;
import com.fxly.demo.system.global.GlobalException;
import com.fxly.demo.system.global.HttpResult;
import com.fxly.demo.system.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件管理Controller
 */
@Slf4j
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
public class SystemFileController {

    @Resource
    private ISystemFileService systemFileService;

    @Operation(summary = "单文件上传")
    @PostMapping("/upload")
    public HttpResult upload(
            @Parameter(description = "文件")
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "子目录（可选）")
            @RequestParam(value = "subPath", required = false) String subPath) {
        try {
            SystemUser currentUser = SecurityUtils.getCurrentLoginUser();
            SystemFile fileInfo = systemFileService.uploadFile(file, subPath, currentUser);
            return HttpResult.success(fileInfo);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new GlobalException(400,"文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "多文件上传")
    @PostMapping("/upload/batch")
    public HttpResult uploadBatch(
            @Parameter(description = "文件列表")
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "子目录（可选）")
            @RequestParam(value = "subPath", required = false) String subPath) {
        try {
            SystemUser currentUser = SecurityUtils.getCurrentLoginUser();
            java.util.List<SystemFile> results = new java.util.ArrayList<>();
            for (MultipartFile file : files) {
                SystemFile fileInfo = systemFileService.uploadFile(file, subPath, currentUser);
                results.add(fileInfo);
            }
            return HttpResult.success(results);
        } catch (Exception e) {
            log.error("批量上传失败", e);
            throw new GlobalException(400,"批量上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "文件下载")
    @GetMapping("/download/{id}")
    public void download(@Parameter(description = "文件ID") @PathVariable("id") Long id, HttpServletResponse response) {
        try {
            SystemFile fileInfo = systemFileService.getById(id);
            if (fileInfo == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在");
                return;
            }

            java.io.File file = systemFileService.getFile(fileInfo.getFilePath());
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在");
                return;
            }

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setContentLengthLong(fileInfo.getFileSize());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8));

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } catch (Exception e) {
            log.error("文件下载失败", e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("文件下载失败: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("写错误响应失败", ioException);
            }
        }
    }

    @Operation(summary = "文件预览")
    @GetMapping("/preview/{id}")
    public void preview(@Parameter(description = "文件ID") @PathVariable("id") Long id, HttpServletResponse response) {
        try {
            SystemFile fileInfo = systemFileService.getById(id);
            if (fileInfo == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在");
                return;
            }

            java.io.File file = systemFileService.getFile(fileInfo.getFilePath());
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在");
                return;
            }

            response.setContentType(fileInfo.getMimeType());
            response.setContentLengthLong(fileInfo.getFileSize());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline");

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } catch (Exception e) {
            log.error("文件预览失败", e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("文件预览失败: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("写错误响应失败", ioException);
            }
        }
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/delete/{id}")
    public HttpResult delete(@Parameter(description = "文件ID") @PathVariable("id") Long id) {
        try {
            boolean success = systemFileService.deleteFile(id);
            return success ? HttpResult.success("删除成功") : HttpResult.error("删除失败");
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new GlobalException(400,"文件删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取文件信息")
    @GetMapping("/info/{id}")
    public HttpResult getInfo(@Parameter(description = "文件ID") @PathVariable("id") Long id) {
        SystemFile fileInfo = systemFileService.getById(id);
        return fileInfo != null ? HttpResult.success(fileInfo) : HttpResult.error("文件不存在");
    }

}
