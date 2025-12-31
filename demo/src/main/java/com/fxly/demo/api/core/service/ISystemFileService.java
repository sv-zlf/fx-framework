package com.fxly.demo.api.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxly.demo.api.core.dto.FileQueryDTO;
import com.fxly.demo.api.core.entity.SystemFile;
import com.fxly.demo.api.core.entity.SystemUser;
import com.fxly.demo.system.global.PageHelper;
import com.fxly.demo.system.global.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件信息服务接口
 */
public interface ISystemFileService extends IService<SystemFile> {

    Page getFileList(FileQueryDTO query);

    /**
     * 上传文件
     * @param file 文件
     * @param subPath 子目录（可选）
     * @param user 当前用户
     * @return 文件信息
     */
    SystemFile uploadFile(MultipartFile file, String subPath, SystemUser user) throws Exception;

    /**
     * 删除文件
     * @param id 文件ID
     * @return 是否成功
     */
    boolean deleteFile(Long id);

    /**
     * 获取文件对象
     * @param relativePath 相对路径
     * @return 文件对象
     */
    File getFile(String relativePath);

}

