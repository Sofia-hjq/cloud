package com.moti.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @ClassName: FtpUtil
 * @Description: FTP工具类
 * @author: xw
 * @date 2020/2/5 10:41
 * @Version: 1.0
 **/
public class FtpUtil {
    private static final Logger logger = Logger.getLogger(FtpUtil.class.getName());
    
    /**
     * FTP服务器hostname
     */
    private static String HOST;
    /**
     * FTP服务器端口
     */
    private static int PORT;
    /**
     * FTP登录账号
     */
    private static String USERNAME;
    /**
     * FTP登录密码
     */
    private static String PASSWORD;
    /**
     * FTP服务器基础目录
     */
    private static String BASEPATH = "";
    /**
     * FTP客户端
     */
    private static FTPClient ftp;

    // 静态初始化块，打印FTP配置信息
    static {
        logger.info("==================== FTP Configuration ====================");
        // 检查系统属性
        String hostProp = System.getProperty("com.moti.utils.FtpUtil.HOST");
        String portProp = System.getProperty("com.moti.utils.FtpUtil.PORT");
        String usernameProp = System.getProperty("com.moti.utils.FtpUtil.USERNAME");
        String passwordProp = System.getProperty("com.moti.utils.FtpUtil.PASSWORD");
        
        logger.info("系统属性 com.moti.utils.FtpUtil.HOST = " + (hostProp != null ? hostProp : "未设置"));
        logger.info("系统属性 com.moti.utils.FtpUtil.PORT = " + (portProp != null ? portProp : "未设置"));
        logger.info("系统属性 com.moti.utils.FtpUtil.USERNAME = " + (usernameProp != null ? usernameProp : "未设置"));
        logger.info("系统属性 com.moti.utils.FtpUtil.PASSWORD = " + (passwordProp != null ? "已设置" : "未设置"));
        
        // 检查环境变量
        String hostEnv = System.getenv("FTP_HOST");
        String portEnv = System.getenv("FTP_PORT");
        String usernameEnv = System.getenv("FTP_USERNAME");
        String passwordEnv = System.getenv("FTP_PASSWORD");
        
        logger.info("环境变量 FTP_HOST = " + (hostEnv != null ? hostEnv : "未设置"));
        logger.info("环境变量 FTP_PORT = " + (portEnv != null ? portEnv : "未设置"));
        logger.info("环境变量 FTP_USERNAME = " + (usernameEnv != null ? usernameEnv : "未设置"));
        logger.info("环境变量 FTP_PASSWORD = " + (passwordEnv != null ? "已设置" : "未设置"));
        
        // 优先使用系统属性，其次是环境变量，最后是默认值
        HOST = hostProp != null ? hostProp : (hostEnv != null ? hostEnv : "localhost");
        PORT = portProp != null ? Integer.parseInt(portProp) : (portEnv != null ? Integer.parseInt(portEnv) : 21);
        USERNAME = usernameProp != null ? usernameProp : (usernameEnv != null ? usernameEnv : "ftpuser");
        PASSWORD = passwordProp != null ? passwordProp : (passwordEnv != null ? passwordEnv : "ftppassword");
        
        // 最终使用的配置
        logger.info("最终 FTP 配置:");
        logger.info("HOST: " + HOST);
        logger.info("PORT: " + PORT);
        logger.info("USERNAME: " + USERNAME);
        logger.info("PASSWORD: " + (PASSWORD != null ? "已设置" : "未设置"));
        logger.info("=========================================================");
    }

    /**
     * @Description 初始化FTP客户端
     * @Author xw
     * @Date 12:34 2020/2/5
     * @Param []
     * @return boolean
     **/
    public static boolean initFtpClient(){
        logger.info("Initializing FTP client with HOST=" + HOST + ", PORT=" + PORT);
        
        // 检查主机名是否可解析
        try {
            logger.info("尝试解析FTP服务器主机名: " + HOST);
            java.net.InetAddress address = java.net.InetAddress.getByName(HOST);
            logger.info("解析成功，IP地址: " + address.getHostAddress());
        } catch (java.net.UnknownHostException e) {
            logger.severe("无法解析FTP服务器主机名: " + HOST + ", 错误: " + e.getMessage());
            return false;
        }
        
        ftp = new FTPClient();
        int reply;
        try {
            // 设置连接超时
            ftp.setConnectTimeout(10000); // 10秒连接超时
            
            // 连接FTP服务器
            logger.info("正在连接FTP服务器: " + HOST + ":" + PORT);
            ftp.connect(HOST, PORT);
            logger.info("Connected to FTP server: " + HOST + ":" + PORT);
            
            //登录, 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            boolean loginSuccess = ftp.login(USERNAME, PASSWORD);
            logger.info("FTP login " + (loginSuccess ? "successful" : "failed"));
            
            if (!loginSuccess) {
                logger.severe("FTP login failed with username: " + USERNAME);
                return false;
            }
            
            ftp.setBufferSize(10240);
            //设置传输超时时间为60秒
            ftp.setDataTimeout(600000);
            //连接超时为60秒
            ftp.setConnectTimeout(600000);
            //FTP以二进制形式传输
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            
            // 设置被动模式
            logger.info("Entering local passive mode");
            ftp.enterLocalPassiveMode();
            
            // 禁用EPSV模式
            ftp.setUseEPSVwithIPv4(false);
            
            // 打印服务器响应信息
            reply = ftp.getReplyCode();
            logger.info("FTP server reply code: " + reply);
            logger.info("FTP server reply string: " + ftp.getReplyString());
            
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.severe("FTP server refused connection. Reply code: " + reply);
                ftp.disconnect();
                return false;
            }
            
            // 测试列出根目录
            try {
                logger.info("尝试列出FTP根目录");
                FTPFile[] files = ftp.listFiles();
                logger.info("根目录文件数量: " + files.length);
            } catch (Exception e) {
                logger.warning("列出根目录失败: " + e.getMessage());
            }
            
        } catch (IOException e) {
            logger.severe("Error initializing FTP client: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * Description: 向FTP服务器上传文件
     * @param filePath FTP服务器文件存放路径。例如分日期存放：/2015/01/01。文件的路径为basePath+filePath
     * @param filename 上传到FTP服务器上的文件名
     * @param input 本地要上传的文件的 输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String filePath, String filename, InputStream input) {
        boolean result = false;
        logger.info("Uploading file: " + filename + " to path: " + filePath);
        
        // 创建输入流的备份以允许重试
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
        
        // 最多尝试3次上传
        int maxRetries = 3;
        int retryCount = 0;
        boolean uploadSuccess = false;
        
        while (!uploadSuccess && retryCount < maxRetries) {
            retryCount++;
            logger.info("Upload attempt " + retryCount + " of " + maxRetries);
            
            try {
                filePath = new String(filePath.getBytes("GBK"),"iso-8859-1");
                filename = new String(filename.getBytes("GBK"),"iso-8859-1");
                
                if (!initFtpClient()){
                    logger.severe("Failed to initialize FTP client");
                    continue; // 尝试下一次重试
                }
                
                // 尝试确保基本路径存在 - 先尝试切换到根目录
                boolean rootDirExists = ftp.changeWorkingDirectory("/");
                if (!rootDirExists) {
                    logger.warning("无法切换到根目录，尝试创建");
                    ftp.makeDirectory("/");
                }
                
                logger.info("Changing working directory to: " + BASEPATH + filePath);
                //切换到上传目录
                if (!ftp.changeWorkingDirectory(BASEPATH+filePath)) {
                    //如果目录不存在创建目录
                    String[] dirs = filePath.split("/");
                    String tempPath = BASEPATH;
                    for (String dir : dirs) {
                        if (null == dir || "".equals(dir)){
                            continue;
                        }
                        tempPath += "/" + dir;
                        logger.info("Checking directory: " + tempPath);
                        if (!ftp.changeWorkingDirectory(tempPath)) {
                            logger.info("Directory doesn't exist, creating: " + tempPath);
                            boolean dirCreated = ftp.makeDirectory(tempPath);
                            logger.info("Directory creation result: " + dirCreated);
                            if (!dirCreated) {
                                logger.severe("Failed to create directory: " + tempPath);
                                break; // 跳出目录创建循环，尝试下一次重试
                            } else {
                                boolean changeResult = ftp.changeWorkingDirectory(tempPath);
                                logger.info("Change to new directory result: " + changeResult);
                                if (!changeResult) {
                                    logger.severe("Created directory but can't change to it: " + tempPath);
                                    break;
                                }
                            }
                        }
                    }
                }
                
                //设置上传文件的类型为二进制类型
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                
                // 确保使用被动模式
                ftp.enterLocalPassiveMode();
                
                // 禁用EPSV模式
                ftp.setUseEPSVwithIPv4(false);
    
                // 打印FTP客户端状态
                logger.info("FTP client connected: " + ftp.isConnected());
                logger.info("Current working directory: " + ftp.printWorkingDirectory());
                
                // 使用备份的输入流进行上传
                InputStream backupInputStream = new ByteArrayInputStream(baos.toByteArray());
                
                // 设置FTP文件传输模式为Stream模式
                ftp.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
                
                //上传文件
                logger.info("Storing file: " + filename);
                boolean storeResult = ftp.storeFile(filename, backupInputStream);
                logger.info("Store file result: " + storeResult + ", Reply code: " + ftp.getReplyCode() + ", Reply string: " + ftp.getReplyString());
                
                if (!storeResult) {
                    logger.severe("Failed to store file: " + filename + ", Reply code: " + ftp.getReplyCode());
                    backupInputStream.close();
                    continue; // 尝试下一次重试
                }
                
                backupInputStream.close();
                ftp.logout();
                logger.info("File uploaded successfully: " + filename);
                uploadSuccess = true;
                result = true;
                break; // 上传成功，跳出重试循环
            }
            catch (IOException e) {
                logger.severe("Error uploading file (attempt " + retryCount + "): " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (ftp != null && ftp.isConnected()) {
                    try {
                        ftp.disconnect();
                    } catch (IOException ioe) {
                        logger.severe("Error disconnecting from FTP server: " + ioe.getMessage());
                    }
                }
            }
            
            // 如果不是最后一次尝试，则等待一段时间后再重试
            if (retryCount < maxRetries) {
                try {
                    logger.info("Waiting before retry...");
                    Thread.sleep(2000); // 等待2秒后重试
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        if (!uploadSuccess) {
            logger.severe("Failed to upload file after " + maxRetries + " attempts");
        }
        
        return result;
    }

    /**
     * Description: 从FTP服务器下载文件
     * @param remotePath FTP服务器上的相对路径
     * @param fileName 要下载的文件名
     * @return
     */
    public static boolean downloadFile( String remotePath,String fileName,String localPath) {
        boolean result = false;

        try {
            remotePath = new String(remotePath.getBytes("GBK"),"iso-8859-1");
            fileName = new String(fileName.getBytes("GBK"),"iso-8859-1");
            if (!initFtpClient()){
                return result;
            };
            // 转移到FTP服务器目录
            ftp.changeWorkingDirectory(remotePath);
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    FileOutputStream outputStream = new FileOutputStream(new File(localPath));
                    ftp.retrieveFile(remotePath+"/"+fileName,outputStream);

                    result = true;
                    outputStream.close();
                }
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }

    /**
     * @Description 从ftp服务器下载文件到指定输出流
     * @Author xw
     * @Date 22:30 2020/3/5
     * @Param [remotePath, fileName, outputStream]
     * @return boolean
     **/
    public static boolean downloadFile(String remotePath, String fileName, OutputStream outputStream) {
        boolean result = false;
        try {
            remotePath = new String(remotePath.getBytes("GBK"),"iso-8859-1");
            fileName = new String(fileName.getBytes("GBK"),"iso-8859-1");
            if (!initFtpClient()){
                return result;
            };
            // 转移到FTP服务器目录
            ftp.changeWorkingDirectory(remotePath);
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    ftp.retrieveFile(remotePath+"/"+fileName,outputStream);
                    result = true;
                }
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }
    
    /**
     * @Description 删除文件
     * @Author xw
     * @Date 11:38 2020/2/6
     * @Param [remotePath, fileName]
     * @return void
     **/
    public static boolean deleteFile( String remotePath,String fileName){
        boolean flag = false;
        try {
            remotePath = new String(remotePath.getBytes("GBK"),"iso-8859-1");
            fileName = new String(fileName.getBytes("GBK"),"iso-8859-1");
            if (!initFtpClient()){
                return flag;
            };
            // 转移到FTP服务器目录
            ftp.changeWorkingDirectory(remotePath);
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if ("".equals(fileName)){
                    return flag;
                }
                if (ff.getName().equals(fileName)){
                    String filePath = remotePath + "/" +fileName;
                    ftp.deleteFile(filePath);
                    flag = true;
                }
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return flag;
    }

    /**
     * @Description 删除文件夹
     * @Author xw
     * @Date 11:38 2020/2/6
     * @Param [remotePath, fileName]
     * @return void
     **/
    public static boolean deleteFolder( String remotePath){
        boolean flag = false;
        try {
            remotePath = new String(remotePath.getBytes("GBK"),"iso-8859-1");
            if (!initFtpClient()){
                return flag;
            };
            // 转移到FTP服务器目录
            ftp.changeWorkingDirectory(remotePath);
            FTPFile[] fs = ftp.listFiles();
            if (fs.length==0){
                ftp.removeDirectory(remotePath);
                flag = true;
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return flag;
    }

    /**
     * @Description 修改文件名称或者文件夹名
     * @Author xw
     * @Date 21:18 2020/2/11
     * @Param [oldAllName, newAllName]
     * @return boolean
     **/
    public static boolean reNameFile( String oldAllName,String newAllName){
        boolean flag = false;
        try {
            oldAllName = new String(oldAllName.getBytes("GBK"),"iso-8859-1");
            newAllName = new String(newAllName.getBytes("GBK"),"iso-8859-1");
            if (!initFtpClient()){
                return flag;
            };
            ftp.rename(oldAllName,newAllName);
            flag = true;
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return flag;
    }
}
