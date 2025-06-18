package com.moti.file.service.impl;

import com.moti.common.entity.MyFile;
import com.moti.common.entity.FileStore;
import com.moti.file.service.FileService;
import com.moti.file.mapper.FileMapper;
import com.moti.file.mapper.FileStoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName: FileServiceImpl
 * @Description: 文件服务实现类
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;
    
    @Autowired
    private FileStoreMapper fileStoreMapper;
    
    @Value("${file.upload-path:D:/moti-cloud-uploads/}")
    private String uploadPath;

    @Override
    public MyFile uploadFile(MultipartFile file, Integer userId, Integer parentFolderId) {
        try {
            // 检查文件仓库容量
            FileStore fileStore = getUserFileStore(userId);
            if (fileStore == null) {
                fileStore = createFileStore(userId);
            }
            
            long fileSize = file.getSize() / 1024; // KB
            if (fileStore.getCurrentSize() + fileSize > fileStore.getMaxSize()) {
                log.error("用户{}文件仓库容量不足，当前使用：{}KB，最大容量：{}KB，本次上传：{}KB", 
                         userId, fileStore.getCurrentSize(), fileStore.getMaxSize(), fileSize);
                return null;
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + postfix;
            
            // 确保上传目录存在 - 使用绝对路径
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    log.error("无法创建上传目录：{}", uploadPath);
                    return null;
                }
                log.info("成功创建上传目录：{}", uploadPath);
            }
            
            String filePath = uploadPath + fileName;
            
            // 保存文件到磁盘
            File targetFile = new File(filePath);
            log.info("准备保存文件到：{}", filePath);
            file.transferTo(targetFile);
            log.info("文件保存成功：{}", filePath);
            
            // 创建文件记录
            MyFile myFile = MyFile.builder()
                    .myFileName(originalFilename)
                    .fileStoreId(fileStore.getFileStoreId())
                    .myFilePath(filePath)
                    .downloadTime(0)
                    .uploadTime(new Date())
                    .parentFolderId(parentFolderId == null ? 0 : parentFolderId)
                    .size((int) fileSize)
                    .type(getFileType(postfix))
                    .postfix(postfix)
                    .build();
            
            // 保存到数据库
            fileMapper.insert(myFile);
            
            // 更新文件仓库使用量
            updateFileStoreSize(userId, (int) fileSize);
            
            log.info("文件上传成功：用户{}, 文件名{}, 大小{}KB", userId, originalFilename, fileSize);
            return myFile;
            
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public MyFile downloadFile(Integer fileId) {
        MyFile file = fileMapper.selectById(fileId);
        if (file != null) {
            // 增加下载次数
            file.setDownloadTime(file.getDownloadTime() + 1);
            fileMapper.updateById(file);
        }
        return file;
    }

    @Override
    public boolean deleteFile(Integer fileId, Integer userId) {
        MyFile file = fileMapper.selectById(fileId);
        if (file == null) {
            return false;
        }
        
        // 验证文件所有权
        FileStore fileStore = fileStoreMapper.selectById(file.getFileStoreId());
        if (!fileStore.getUserId().equals(userId)) {
            return false;
        }
        
        // 删除物理文件
        File physicalFile = new File(file.getMyFilePath());
        if (physicalFile.exists()) {
            physicalFile.delete();
        }
        
        // 删除数据库记录
        fileMapper.deleteById(fileId);
        
        // 更新文件仓库使用量
        updateFileStoreSize(userId, -file.getSize());
        
        return true;
    }

    @Override
    public List<MyFile> getUserFiles(Integer userId, Integer parentFolderId) {
        return fileMapper.selectByUserIdAndFolder(userId, parentFolderId);
    }

    @Override
    public List<MyFile> getAllUserFiles(Integer userId) {
        return fileMapper.selectAllByUserId(userId);
    }

    @Override
    public List<MyFile> getFilesByType(Integer userId, Integer type) {
        return fileMapper.selectByUserIdAndType(userId, type);
    }

    @Override
    public FileStore createFileStore(Integer userId) {
        FileStore fileStore = FileStore.builder()
                .userId(userId)
                .currentSize(0)
                .maxSize(1024 * 1024) // 默认1GB
                .permission(0)
                .build();
        
        fileStoreMapper.insert(fileStore);
        return fileStore;
    }

    @Override
    public FileStore getUserFileStore(Integer userId) {
        return fileStoreMapper.selectByUserId(userId);
    }

    @Override
    public boolean updateFileStoreSize(Integer userId, Integer sizeChange) {
        return fileStoreMapper.updateSize(userId, sizeChange) > 0;
    }

    @Override
    public List<MyFile> searchFiles(Integer userId, String keyword) {
        return fileMapper.searchByKeyword(userId, keyword);
    }

    @Override
    public boolean updateFileName(MyFile file, Integer userId) {
        try {
            // 验证文件所有权
            FileStore fileStore = fileStoreMapper.selectById(file.getFileStoreId());
            if (!fileStore.getUserId().equals(userId)) {
                return false;
            }
            
            // 更新文件名
            return fileMapper.updateById(file) > 0;
        } catch (Exception e) {
            log.error("更新文件名失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 根据文件后缀判断文件类型
     * 1-文档 2-图片 3-视频 4-音乐 5-其他
     */
    private Integer getFileType(String postfix) {
        postfix = postfix.toLowerCase();
        if (postfix.matches("\\.(txt|doc|docx|pdf|xls|xlsx|ppt|pptx)")) {
            return 1; // 文档
        } else if (postfix.matches("\\.(jpg|jpeg|png|gif|bmp|svg)")) {
            return 2; // 图片
        } else if (postfix.matches("\\.(mp4|avi|mov|wmv|flv|mkv)")) {
            return 3; // 视频
        } else if (postfix.matches("\\.(mp3|wav|flac|aac|ogg)")) {
            return 4; // 音乐
        } else {
            return 5; // 其他
        }
    }
} 