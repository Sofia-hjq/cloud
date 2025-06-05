package com.moti.utils;

import java.io.*;
import java.util.logging.Logger;

/**
 * @ClassName: FileSystemUtil
 * @Description: 本地文件系统工具类，作为FTP的替代方案
 * @author: xw
 * @date 2025/6/5
 * @Version: 1.0
 **/
public class FileSystemUtil {
    private static final Logger logger = Logger.getLogger(FileSystemUtil.class.getName());
    
    /**
     * 文件存储根目录
     */
    private static String ROOT_PATH = System.getProperty("com.moti.utils.FileSystemUtil.ROOT_PATH", "uploads");
    
    static {
        logger.info("==================== File System Configuration ====================");
        logger.info("ROOT_PATH: " + ROOT_PATH);
        
        // 确保根目录存在
        File rootDir = new File(ROOT_PATH);
        if (!rootDir.exists()) {
            boolean created = rootDir.mkdirs();
            logger.info("Created root directory: " + created);
        }
        logger.info("=================================================================");
    }
    
    /**
     * 向本地文件系统上传文件
     * @param filePath 文件存放路径，相对于根目录
     * @param filename 文件名
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String filePath, String filename, InputStream input) {
        boolean result = false;
        logger.info("Uploading file to local filesystem: " + filename + " to path: " + filePath);
        
        // 创建输入流的备份
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            input.close();
        } catch (IOException e) {
            logger.severe("Error reading input stream: " + e.getMessage());
            return false;
        }
        
        // 创建目录
        File dir = new File(ROOT_PATH + filePath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            logger.info("Created directory structure: " + created);
            if (!created) {
                logger.severe("Failed to create directory: " + dir.getAbsolutePath());
                return false;
            }
        }
        
        // 创建文件
        File file = new File(dir, filename);
        try (FileOutputStream fos = new FileOutputStream(file);
             InputStream backupInputStream = new ByteArrayInputStream(baos.toByteArray())) {
            
            buffer = new byte[1024];
            while ((len = backupInputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            
            logger.info("File uploaded successfully: " + file.getAbsolutePath());
            result = true;
        } catch (IOException e) {
            logger.severe("Error writing file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 从本地文件系统下载文件
     * @param remotePath 文件路径，相对于根目录
     * @param fileName 文件名
     * @param outputStream 输出流
     * @return 成功返回true，否则返回false
     */
    public static boolean downloadFile(String remotePath, String fileName, OutputStream outputStream) {
        boolean result = false;
        logger.info("Downloading file from local filesystem: " + fileName + " from path: " + remotePath);
        
        File file = new File(ROOT_PATH + remotePath, fileName);
        if (!file.exists() || !file.isFile()) {
            logger.severe("File not found: " + file.getAbsolutePath());
            return false;
        }
        
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            
            logger.info("File downloaded successfully: " + file.getAbsolutePath());
            result = true;
        } catch (IOException e) {
            logger.severe("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 从本地文件系统下载文件到本地路径
     * @param remotePath 文件路径，相对于根目录
     * @param fileName 文件名
     * @param localPath 本地保存路径
     * @return 成功返回true，否则返回false
     */
    public static boolean downloadFile(String remotePath, String fileName, String localPath) {
        boolean result = false;
        logger.info("Downloading file from local filesystem to local path: " + fileName);
        
        try (FileOutputStream fos = new FileOutputStream(new File(localPath))) {
            result = downloadFile(remotePath, fileName, fos);
        } catch (IOException e) {
            logger.severe("Error creating output stream: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 删除本地文件系统中的文件
     * @param remotePath 文件路径，相对于根目录
     * @param fileName 文件名
     * @return 成功返回true，否则返回false
     */
    public static boolean deleteFile(String remotePath, String fileName) {
        logger.info("Deleting file from local filesystem: " + fileName + " from path: " + remotePath);
        
        File file = new File(ROOT_PATH + remotePath, fileName);
        if (!file.exists() || !file.isFile()) {
            logger.warning("File not found for deletion: " + file.getAbsolutePath());
            return false;
        }
        
        boolean deleted = file.delete();
        logger.info("File deleted: " + deleted);
        
        return deleted;
    }
    
    /**
     * 删除本地文件系统中的文件夹
     * @param remotePath 文件夹路径，相对于根目录
     * @return 成功返回true，否则返回false
     */
    public static boolean deleteFolder(String remotePath) {
        logger.info("Deleting folder from local filesystem: " + remotePath);
        
        File dir = new File(ROOT_PATH + remotePath);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warning("Folder not found for deletion: " + dir.getAbsolutePath());
            return false;
        }
        
        // 检查目录是否为空
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            logger.warning("Folder is not empty: " + dir.getAbsolutePath());
            return false;
        }
        
        boolean deleted = dir.delete();
        logger.info("Folder deleted: " + deleted);
        
        return deleted;
    }
    
    /**
     * 重命名本地文件系统中的文件
     * @param oldAllName 旧的完整路径和文件名
     * @param newAllName 新的完整路径和文件名
     * @return 成功返回true，否则返回false
     */
    public static boolean reNameFile(String oldAllName, String newAllName) {
        logger.info("Renaming file in local filesystem from: " + oldAllName + " to: " + newAllName);
        
        File oldFile = new File(ROOT_PATH + oldAllName);
        File newFile = new File(ROOT_PATH + newAllName);
        
        if (!oldFile.exists()) {
            logger.warning("Source file not found: " + oldFile.getAbsolutePath());
            return false;
        }
        
        if (newFile.exists()) {
            logger.warning("Target file already exists: " + newFile.getAbsolutePath());
            return false;
        }
        
        // 确保目标目录存在
        File parentDir = newFile.getParentFile();
        if (!parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            logger.info("Created target directory: " + created);
        }
        
        boolean renamed = oldFile.renameTo(newFile);
        logger.info("File renamed: " + renamed);
        
        return renamed;
    }
} 