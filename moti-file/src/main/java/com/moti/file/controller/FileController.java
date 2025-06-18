package com.moti.file.controller;

import com.moti.common.entity.MyFile;
import com.moti.common.entity.FileStore;
import com.moti.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: FileController
 * @Description: 文件管理控制器
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Slf4j
@Controller
@RequestMapping("/u-admin")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 用户主页 - 使用情况统计
     */
    @GetMapping("")
    public String index(Model model, HttpServletRequest request) {
        Map<String, Object> loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/";
        }
        
        // 设置session中的loginUser
        request.getSession().setAttribute("loginUser", loginUser);
        
        // 创建统计数据
        Map<String, Object> statistics = createDefaultStatistics();
        model.addAttribute("statistics", statistics);
        
        return "u-admin/index";
    }

    /**
     * 所有文件页面
     */
    @GetMapping("/files")
    public String files(Model model, HttpServletRequest request) {
        Map<String, Object> loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/";
        }
        
        request.getSession().setAttribute("loginUser", loginUser);
        Map<String, Object> statistics = createDefaultStatistics();
        model.addAttribute("statistics", statistics);
        
        // 获取用户所有文件列表（所有文件，不限制目录）
        Integer userId = (Integer) loginUser.get("userId");
        try {
            List<MyFile> files = fileService.getAllUserFiles(userId); // 获取所有文件
            model.addAttribute("files", files != null ? files : new java.util.ArrayList<>());
            log.info("为用户{}加载了{}个文件", userId, files != null ? files.size() : 0);
        } catch (Exception e) {
            log.error("获取用户文件失败：{}", e.getMessage());
            model.addAttribute("files", new java.util.ArrayList<>());
        }
        
        // 添加文件页面需要的模型属性
        Map<String, Object> nowFolder = createDefaultFolder();
        model.addAttribute("nowFolder", nowFolder);
        model.addAttribute("location", new java.util.ArrayList<>());
        model.addAttribute("folders", new java.util.ArrayList<>());
        model.addAttribute("permission", 1); // 默认权限
        
        return "u-admin/files";
    }

    /**
     * 上传文件页面
     */
    @GetMapping("/upload")
    public String upload(Model model, HttpServletRequest request) {
        Map<String, Object> loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/";
        }
        
        request.getSession().setAttribute("loginUser", loginUser);
        Map<String, Object> statistics = createDefaultStatistics();
        model.addAttribute("statistics", statistics);
        
        // 添加上传页面需要的模型属性
        Map<String, Object> nowFolder = createDefaultFolder();
        model.addAttribute("nowFolder", nowFolder);
        model.addAttribute("location", new java.util.ArrayList<>());
        model.addAttribute("folders", new java.util.ArrayList<>());
        
        return "u-admin/upload";
    }

    /**
     * 文档文件页面
     */
    @GetMapping("/doc-files")
    public String docFiles(Model model, HttpServletRequest request) {
        Map<String, Object> loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/";
        }
        
        request.getSession().setAttribute("loginUser", loginUser);
        Map<String, Object> statistics = createDefaultStatistics();
        model.addAttribute("statistics", statistics);
        
        // 获取用户的文档文件列表
        Integer userId = (Integer) loginUser.get("userId");
        try {
            List<MyFile> files = fileService.getFilesByType(userId, 1); // 1-文档
            model.addAttribute("files", files != null ? files : new java.util.ArrayList<>());
        } catch (Exception e) {
            log.error("获取文档文件失败：{}", e.getMessage());
            model.addAttribute("files", new java.util.ArrayList<>());
        }
        
        return "u-admin/doc-files";
    }

    /**
     * 图片文件页面
     */
    @GetMapping("/image-files")
    public String imageFiles(Model model, HttpServletRequest request) {
        Map<String, Object> loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/";
        }
        
        request.getSession().setAttribute("loginUser", loginUser);
        Map<String, Object> statistics = createDefaultStatistics();
        model.addAttribute("statistics", statistics);
        
        // 获取用户的图片文件列表
        Integer userId = (Integer) loginUser.get("userId");
        try {
            List<MyFile> files = fileService.getFilesByType(userId, 2); // 2-图片
            model.addAttribute("files", files != null ? files : new java.util.ArrayList<>());
        } catch (Exception e) {
            log.error("获取图片文件失败：{}", e.getMessage());
            model.addAttribute("files", new java.util.ArrayList<>());
        }
        
        return "u-admin/image-files";
    }

    /**
     * 视频文件页面
     */
    @GetMapping("/video-files")
    public String videoFiles(Model model, HttpServletRequest request) {
        Map<String, Object> loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/";
        }
        
        request.getSession().setAttribute("loginUser", loginUser);
        Map<String, Object> statistics = createDefaultStatistics();
        model.addAttribute("statistics", statistics);
        
        // 获取用户的视频文件列表
        Integer userId = (Integer) loginUser.get("userId");
        try {
            List<MyFile> files = fileService.getFilesByType(userId, 3); // 3-视频
            model.addAttribute("files", files != null ? files : new java.util.ArrayList<>());
        } catch (Exception e) {
            log.error("获取视频文件失败：{}", e.getMessage());
            model.addAttribute("files", new java.util.ArrayList<>());
        }
        
        return "u-admin/video-files";
    }

    /**
     * 音频文件页面
     */
    @GetMapping("/music-files")
    public String musicFiles(Model model, HttpServletRequest request) {
        Map<String, Object> loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/";
        }
        
        request.getSession().setAttribute("loginUser", loginUser);
        Map<String, Object> statistics = createDefaultStatistics();
        model.addAttribute("statistics", statistics);
        
        // 获取用户的音频文件列表
        Integer userId = (Integer) loginUser.get("userId");
        try {
            List<MyFile> files = fileService.getFilesByType(userId, 4); // 4-音频
            model.addAttribute("files", files != null ? files : new java.util.ArrayList<>());
        } catch (Exception e) {
            log.error("获取音频文件失败：{}", e.getMessage());
            model.addAttribute("files", new java.util.ArrayList<>());
        }
        
        return "u-admin/music-files";
    }

    /**
     * 其他文件页面
     */
    @GetMapping("/other-files")
    public String otherFiles(Model model, HttpServletRequest request) {
        Map<String, Object> loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/";
        }
        
        request.getSession().setAttribute("loginUser", loginUser);
        Map<String, Object> statistics = createDefaultStatistics();
        model.addAttribute("statistics", statistics);
        
        // 获取用户的其他文件列表
        Integer userId = (Integer) loginUser.get("userId");
        try {
            List<MyFile> files = fileService.getFilesByType(userId, 5); // 5-其他
            model.addAttribute("files", files != null ? files : new java.util.ArrayList<>());
        } catch (Exception e) {
            log.error("获取其他文件失败：{}", e.getMessage());
            model.addAttribute("files", new java.util.ArrayList<>());
        }
        
        return "u-admin/other-files";
    }

    /**
     * 使用帮助页面
     */
    @GetMapping("/help")
    public String help(Model model, HttpServletRequest request) {
        Map<String, Object> loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/";
        }
        
        request.getSession().setAttribute("loginUser", loginUser);
        Map<String, Object> statistics = createDefaultStatistics();
        model.addAttribute("statistics", statistics);
        
        return "u-admin/help";
    }

    /**
     * 获取登录用户信息
     */
    private Map<String, Object> getLoginUser(HttpServletRequest request) {
        return (Map<String, Object>) request.getSession().getAttribute("currentUser");
    }

    /**
     * 创建默认统计数据
     */
    private Map<String, Object> createDefaultStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("folderCount", 0);
        statistics.put("fileCount", 0);
        statistics.put("doc", 0);
        statistics.put("image", 0);
        statistics.put("video", 0);
        statistics.put("music", 0);
        statistics.put("other", 0);
        
        // 文件仓库信息
        Map<String, Object> fileStore = new HashMap<>();
        fileStore.put("currentSize", 0);
        fileStore.put("maxSize", 5242880); // 5GB
        statistics.put("fileStore", fileStore);
        
        return statistics;
    }
    
    /**
     * 创建默认文件夹信息
     */
    private Map<String, Object> createDefaultFolder() {
        Map<String, Object> folder = new HashMap<>();
        folder.put("fileFolderId", 0);
        folder.put("parentFolderId", 0);
        folder.put("fileFolderName", "根目录");
        return folder;
    }

    /**
     * 文件上传接口 - 兼容u-admin路径
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file,
                                        HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取当前登录用户
            Map<String, Object> loginUser = getLoginUser(request);
            if (loginUser == null) {
                result.put("code", 401);
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }
            
            if (file.isEmpty()) {
                result.put("code", 400);
                result.put("success", false);
                result.put("message", "上传文件不能为空");
                return result;
            }
            
            // 获取用户ID和父文件夹ID
            Integer userId = (Integer) loginUser.get("userId");
            String folderIdHeader = request.getHeader("id");
            Integer parentFolderId = null;
            if (folderIdHeader != null && !folderIdHeader.isEmpty() && !"0".equals(folderIdHeader)) {
                try {
                    parentFolderId = Integer.parseInt(folderIdHeader);
                } catch (NumberFormatException e) {
                    parentFolderId = null;
                }
            }
            
            // 调用文件服务上传文件
            MyFile uploadedFile = fileService.uploadFile(file, userId, parentFolderId);
            if (uploadedFile != null) {
                result.put("code", 200);
                result.put("success", true);
                result.put("message", "文件上传成功");
                result.put("file", uploadedFile);
            } else {
                // 检查具体的失败原因
                try {
                    FileStore fileStore = fileService.getUserFileStore(userId);
                    if (fileStore == null) {
                        // 尝试创建文件仓库
                        fileStore = fileService.createFileStore(userId);
                        if (fileStore == null) {
                            result.put("code", 504);
                            result.put("success", false);
                            result.put("message", "创建文件仓库失败，请联系管理员");
                            return result;
                        }
                    }
                    
                    long fileSize = file.getSize() / 1024; // KB
                    long currentSize = fileStore.getCurrentSize();
                    long maxSize = fileStore.getMaxSize();
                    
                    if (currentSize + fileSize > maxSize) {
                        result.put("code", 503);
                        result.put("success", false);
                        result.put("message", String.format("仓库容量不足！当前已用 %dKB，最大容量 %dKB，本次上传 %dKB", 
                                                           currentSize, maxSize, fileSize));
                        result.put("currentSize", currentSize);
                        result.put("maxSize", maxSize);
                        result.put("fileSize", fileSize);
                    } else {
                        result.put("code", 504);
                        result.put("success", false);
                        result.put("message", "服务器内部错误，上传失败");
                    }
                } catch (Exception ex) {
                    log.error("检查上传失败原因时出错：{}", ex.getMessage());
                    result.put("code", 504);
                    result.put("success", false);
                    result.put("message", "服务器出错了！错误详情：" + ex.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage());
            result.put("code", 504);
            result.put("success", false);
            result.put("message", "服务器出错了！");
        }
        
        return result;
    }
} 