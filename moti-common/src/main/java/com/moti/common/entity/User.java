package com.moti.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (User)用户实体类
 *
 * @author 莫提
 * @since 2020-02-25 17:19:04
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User implements Serializable {
    /**
    * 用户ID
    */
    private Integer userId;
    /**
    * 用户的openid
    */
    private String openId;
    /**
    * 文件仓库ID
    */
    private Integer fileStoreId;
    /**
    * 用户名
    */
    private String userName;
    /**
     * 用户密码
     */
    private String password;
    /**
    * 注册时间
    */
    private Date registerTime;
    /**
    * 头像地址
    */
    private String imagePath;
    /**
     * 用户角色，0管理员，1普通用户
     */
    private Integer role;
    
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