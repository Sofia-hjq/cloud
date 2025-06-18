package com.moti.file.service;

import com.moti.common.entity.MyFile;
import com.moti.common.entity.FileStore;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @ClassName: FileService
 * @Description: 文件服务接口
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
public interface FileService {
    
    /**
     * 文件上传
     */
    MyFile uploadFile(MultipartFile file, Integer userId, Integer parentFolderId);
    
    /**
     * 文件下载（获取文件信息）
     */
    MyFile downloadFile(Integer fileId);
    
    /**
     * 删除文件
     */
    boolean deleteFile(Integer fileId, Integer userId);
    
    /**
     * 获取用户文件列表
     */
    List<MyFile> getUserFiles(Integer userId, Integer parentFolderId);
    
    /**
     * 获取用户所有文件列表
     */
    List<MyFile> getAllUserFiles(Integer userId);
    
    /**
     * 根据文件类型获取文件列表
     */
    List<MyFile> getFilesByType(Integer userId, Integer type);
    
    /**
     * 创建用户文件仓库
     */
    FileStore createFileStore(Integer userId);
    
    /**
     * 获取用户文件仓库信息
     */
    FileStore getUserFileStore(Integer userId);
    
    /**
     * 更新文件仓库使用量
     */
    boolean updateFileStoreSize(Integer userId, Integer sizeChange);
    
    /**
     * 搜索文件
     */
    List<MyFile> searchFiles(Integer userId, String keyword);
    
    /**
     * 更新文件名
     */
    boolean updateFileName(MyFile file, Integer userId);
} 