package com.moti.file.controller;

import com.moti.common.entity.MyFile;
import com.moti.common.entity.FileStore;
import com.moti.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: FileApiController
 * @Description: 文件API控制器 - 提供RESTful接口
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Slf4j
@RestController
@RequestMapping("/file")
public class FileApiController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("userId") Integer userId,
                                         @RequestParam(value = "parentFolderId", required = false) Integer parentFolderId) {
        Map<String, Object> result = new HashMap<>();
        
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "上传文件不能为空");
            return result;
        }
        
        MyFile uploadedFile = fileService.uploadFile(file, userId, parentFolderId);
        if (uploadedFile != null) {
            result.put("success", true);
            result.put("message", "文件上传成功");
            result.put("file", uploadedFile);
        } else {
            result.put("success", false);
            result.put("message", "文件上传失败，请检查仓库容量");
        }
        
        return result;
    }

    /**
     * 文件下载
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer fileId) {
        MyFile myFile = fileService.downloadFile(fileId);
        if (myFile == null) {
            return ResponseEntity.notFound().build();
        }
        
        File file = new File(myFile.getMyFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new FileSystemResource(file);
        
        try {
            String encodedFileName = URLEncoder.encode(myFile.getMyFileName(), "UTF-8");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (UnsupportedEncodingException e) {
            log.error("文件名编码失败：{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete/{fileId}")
    public Map<String, Object> deleteFile(@PathVariable Integer fileId,
                                         @RequestParam("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        boolean success = fileService.deleteFile(fileId, userId);
        result.put("success", success);
        result.put("message", success ? "文件删除成功" : "文件删除失败");
        
        return result;
    }

    /**
     * 获取用户文件列表
     */
    @GetMapping("/list")
    public Map<String, Object> getUserFiles(@RequestParam("userId") Integer userId,
                                           @RequestParam(value = "parentFolderId", defaultValue = "0") Integer parentFolderId) {
        Map<String, Object> result = new HashMap<>();
        
        List<MyFile> files = fileService.getUserFiles(userId, parentFolderId);
        result.put("success", true);
        result.put("files", files);
        
        return result;
    }

    /**
     * 根据类型获取文件列表
     */
    @GetMapping("/list/type/{type}")
    public Map<String, Object> getFilesByType(@PathVariable Integer type,
                                             @RequestParam("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        List<MyFile> files = fileService.getFilesByType(userId, type);
        result.put("success", true);
        result.put("files", files);
        result.put("type", type);
        
        return result;
    }

    /**
     * 文件搜索
     */
    @GetMapping("/search")
    public Map<String, Object> searchFiles(@RequestParam("userId") Integer userId,
                                          @RequestParam("keyword") String keyword) {
        Map<String, Object> result = new HashMap<>();
        
        List<MyFile> files = fileService.searchFiles(userId, keyword);
        result.put("success", true);
        result.put("files", files);
        result.put("keyword", keyword);
        
        return result;
    }

    /**
     * 获取用户文件仓库信息
     */
    @GetMapping("/store")
    public Map<String, Object> getFileStore(@RequestParam("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        FileStore fileStore = fileService.getUserFileStore(userId);
        if (fileStore == null) {
            fileStore = fileService.createFileStore(userId);
        }
        
        result.put("success", true);
        result.put("fileStore", fileStore);
        
        // 计算使用率
        double usage = (double) fileStore.getCurrentSize() / fileStore.getMaxSize() * 100;
        result.put("usagePercentage", Math.round(usage * 100.0) / 100.0);
        
        return result;
    }

    /**
     * 初始化用户文件仓库
     */
    @PostMapping("/store/init")
    public Map<String, Object> initFileStore(@RequestParam("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            FileStore fileStore = fileService.getUserFileStore(userId);
            if (fileStore == null) {
                fileStore = fileService.createFileStore(userId);
                if (fileStore != null) {
                    result.put("success", true);
                    result.put("message", "文件仓库初始化成功");
                    result.put("fileStore", fileStore);
                } else {
                    result.put("success", false);
                    result.put("message", "文件仓库创建失败");
                }
            } else {
                result.put("success", true);
                result.put("message", "文件仓库已存在");
                result.put("fileStore", fileStore);
            }
        } catch (Exception e) {
            log.error("初始化文件仓库失败：{}", e.getMessage());
            result.put("success", false);
            result.put("message", "初始化失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取文件详情
     */
    @GetMapping("/info/{fileId}")
    public Map<String, Object> getFileInfo(@PathVariable Integer fileId) {
        Map<String, Object> result = new HashMap<>();
        
        MyFile file = fileService.downloadFile(fileId);
        if (file != null) {
            result.put("success", true);
            result.put("file", file);
        } else {
            result.put("success", false);
            result.put("message", "文件不存在");
        }
        
        return result;
    }

    /**
     * 重命名文件
     */
    @PutMapping("/rename")
    public Map<String, Object> renameFile(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        Integer fileId = (Integer) request.get("fileId");
        String newName = (String) request.get("newName");
        Integer userId = (Integer) request.get("userId");
        
        if (fileId == null || newName == null || userId == null) {
            result.put("success", false);
            result.put("message", "参数不完整");
            return result;
        }
        
        // 获取文件信息并验证所有权
        MyFile file = fileService.downloadFile(fileId);
        if (file == null) {
            result.put("success", false);
            result.put("message", "文件不存在");
            return result;
        }
        
        // 更新文件名
        file.setMyFileName(newName);
        boolean success = fileService.updateFileName(file, userId);
        
        result.put("success", success);
        result.put("message", success ? "重命名成功" : "重命名失败");
        
        return result;
    }
} 