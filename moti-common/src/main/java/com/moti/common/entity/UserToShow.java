package com.moti.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: UserToShow
 * @Description: 用于前端显示
 * @author: xw
 * @date 2020/3/10 16:31
 * @Version: 1.0
 **/
@AllArgsConstructor
@Data
@Builder
public class UserToShow {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 注册时间
     */
    private Date registerTime;
    /**
     * 头像地址
     */
    private String imagePath;
    /**
     * 当前已使用百分比
     */
    private Integer current;
    /**
     * 仓库最大容量（单位KB）
     */
    private Integer maxSize;
    /**
     * 仓库权限：0可上传下载、1只允许下载、2禁止上传下载
     */
    private Integer permission;
    
    /**
     * 获取用户头像显示字符（用户名的首字符）
     * @return 用户名的首字符，用于显示头像
     */
    public String getAvatarChar() {
        if (userName == null || userName.trim().isEmpty()) {
            return "U"; // 默认字符
        }
        // 取用户名的第一个字符
        String firstChar = userName.trim().substring(0, 1).toUpperCase();
        // 如果是中文字符，直接返回；如果是英文，返回大写
        return firstChar;
    }
    
    /**
     * 获取头像颜色（基于用户名hash值）
     * @return 头像背景颜色的CSS类名
     */
    public String getAvatarColor() {
        if (userName == null || userName.trim().isEmpty()) {
            return "avatar-color-1";
        }
        // 基于用户名的hash值选择颜色
        int hash = Math.abs(userName.hashCode());
        int colorIndex = (hash % 8) + 1; // 1-8之间的颜色
        return "avatar-color-" + colorIndex;
    }
}
