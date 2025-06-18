package com.moti.file.service.impl;

import com.moti.common.entity.MyFile;
import com.moti.common.entity.FileStore;
import com.moti.common.entity.FileStoreStatistics;
import com.moti.file.service.FileService;
import com.moti.file.mapper.FileMapper;
import com.moti.file.mapper.FileStoreMapper;
import com.moti.file.mapper.FileFolderMapper;
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
    
    @Autowired
    private FileFolderMapper fileFolderMapper;
    
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

    @Override
    public FileStoreStatistics getUserFileStatistics(Integer userId) {
        try {
            // 获取文件仓库信息
            FileStore fileStore = getUserFileStore(userId);
            if (fileStore == null) {
                fileStore = createFileStore(userId);
            }
            
            // 获取文件总数
            int fileCount = fileMapper.countFilesByUserId(userId);
            
            // 获取各类型文件数量 (1-文档, 2-图像, 3-视频, 4-音频, 5-其他)
            int docCount = fileMapper.countFilesByUserIdAndType(userId, 1);
            int imageCount = fileMapper.countFilesByUserIdAndType(userId, 2);
            int videoCount = fileMapper.countFilesByUserIdAndType(userId, 3);
            int musicCount = fileMapper.countFilesByUserIdAndType(userId, 4);
            int otherCount = fileMapper.countFilesByUserIdAndType(userId, 5);
            
            // 获取文件夹数量
            int folderCount = fileFolderMapper.countByUserId(userId);
            
            // 构建统计对象
            return FileStoreStatistics.builder()
                    .fileStore(fileStore)
                    .fileCount(fileCount)
                    .doc(docCount)
                    .image(imageCount)
                    .video(videoCount)
                    .music(musicCount)
                    .other(otherCount)
                    .folderCount(folderCount)
                    .build();
                    
        } catch (Exception e) {
            log.error("获取用户文件统计数据失败：{}", e.getMessage());
            // 返回默认统计数据
            FileStore defaultFileStore = FileStore.builder()
                    .userId(userId)
                    .currentSize(0)
                    .maxSize(1024 * 1024)
                    .permission(0)
                    .build();
                    
            return FileStoreStatistics.builder()
                    .fileStore(defaultFileStore)
                    .fileCount(0)
                    .doc(0)
                    .image(0)
                    .video(0)
                    .music(0)
                    .other(0)
                    .folderCount(0)
                    .build();
        }
    }

    /**
     * 根据文件后缀名判断文件类型
     */
    private Integer getFileType(String postfix) {
        if (postfix == null) return 5; // 其他
        
        String lowerPostfix = postfix.toLowerCase();
        
        // 文档类型
        if (lowerPostfix.matches("\\.(doc|docx|pdf|txt|xls|xlsx|ppt|pptx)")) {
            return 1;
        }
        // 图像类型
        else if (lowerPostfix.matches("\\.(jpg|jpeg|png|gif|bmp|webp|svg)")) {
            return 2;
        }
        // 视频类型
        else if (lowerPostfix.matches("\\.(mp4|avi|mkv|mov|wmv|flv|webm)")) {
            return 3;
        }
        // 音频类型
        else if (lowerPostfix.matches("\\.(mp3|wav|flac|aac|wma|ogg)")) {
            return 4;
        }
        // 其他类型
        else {
            return 5;
        }
    }
    
    @Override
    public com.moti.common.entity.FileFolder createFolder(String folderName, Integer parentFolderId, Integer userId) {
        try {
            // 获取用户的文件仓库
            FileStore fileStore = getUserFileStore(userId);
            if (fileStore == null) {
                fileStore = createFileStore(userId);
            }
            
            // 创建文件夹对象
            com.moti.common.entity.FileFolder folder = com.moti.common.entity.FileFolder.builder()
                    .fileFolderName(folderName)
                    .parentFolderId(parentFolderId == null ? 0 : parentFolderId)
                    .fileStoreId(fileStore.getFileStoreId())
                    .time(new Date())
                    .build();
            
            // 保存到数据库
            fileFolderMapper.insert(folder);
            
            log.info("文件夹创建成功：用户{}, 文件夹名{}, 父文件夹ID{}", userId, folderName, parentFolderId);
            return folder;
            
        } catch (Exception e) {
            log.error("创建文件夹失败：{}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean updateFolder(Integer folderId, String folderName, Integer userId) {
        try {
            // 获取文件夹信息
            com.moti.common.entity.FileFolder folder = fileFolderMapper.selectById(folderId);
            if (folder == null) {
                return false;
            }
            
            // 验证文件夹所有权
            FileStore fileStore = fileStoreMapper.selectById(folder.getFileStoreId());
            if (!fileStore.getUserId().equals(userId)) {
                return false;
            }
            
            // 更新文件夹名称
            folder.setFileFolderName(folderName);
            fileFolderMapper.updateById(folder);
            
            log.info("文件夹更新成功：用户{}, 文件夹ID{}, 新名称{}", userId, folderId, folderName);
            return true;
            
        } catch (Exception e) {
            log.error("更新文件夹失败：{}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean deleteFolder(Integer folderId, Integer userId) {
        try {
            // 获取文件夹信息
            com.moti.common.entity.FileFolder folder = fileFolderMapper.selectById(folderId);
            if (folder == null) {
                return false;
            }
            
            // 验证文件夹所有权
            FileStore fileStore = fileStoreMapper.selectById(folder.getFileStoreId());
            if (!fileStore.getUserId().equals(userId)) {
                return false;
            }
            
            // 删除文件夹（需要先删除文件夹内的文件和子文件夹）
            // 这里简单实现，实际应该递归删除
            fileFolderMapper.deleteById(folderId);
            
            log.info("文件夹删除成功：用户{}, 文件夹ID{}", userId, folderId);
            return true;
            
        } catch (Exception e) {
            log.error("删除文件夹失败：{}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<com.moti.common.entity.FileFolder> getUserFolders(Integer userId, Integer parentFolderId) {
        try {
            Integer actualParentFolderId = parentFolderId == null ? 0 : parentFolderId;
            log.info("正在查询用户{}在父文件夹{}下的文件夹列表", userId, actualParentFolderId);
            
            List<com.moti.common.entity.FileFolder> folders = fileFolderMapper.selectByUserIdAndParent(userId, actualParentFolderId);
            
            log.info("查询结果：用户{}在父文件夹{}下找到{}个文件夹", userId, actualParentFolderId, folders != null ? folders.size() : 0);
            if (folders != null && !folders.isEmpty()) {
                for (com.moti.common.entity.FileFolder folder : folders) {
                    log.info("文件夹详情：ID={}, 名称={}, 父文件夹ID={}, 文件仓库ID={}", 
                            folder.getFileFolderId(), folder.getFileFolderName(), 
                            folder.getParentFolderId(), folder.getFileStoreId());
                }
            }
            
            return folders;
        } catch (Exception e) {
            log.error("查询用户文件夹失败：用户ID={}, 父文件夹ID={}, 错误：{}", userId, parentFolderId, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
} 