package com.moti.user.controller;

import com.moti.common.entity.User;
import com.moti.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: UserController
 * @Description: 用户控制器
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户名是否已注册
        User existingUser = userService.getUserByUserName(user.getUserName());
        if (existingUser != null) {
            result.put("success", false);
            result.put("message", "该用户名已被注册");
            return result;
        }
        
        // 用户名去空格
        user.setUserName(user.getUserName().trim());
        user.setImagePath("/static/img/default-avatar.png");
        user.setRegisterTime(new Date());
        user.setRole(1); // 普通用户
        
        boolean success = userService.register(user);
        if (success) {
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("user", user);
        } else {
            result.put("success", false);
            result.put("message", "注册失败");
        }
        
        return result;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        
        User loginUser = userService.login(user.getUserName(), user.getPassword());
        if (loginUser != null) {
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("user", loginUser);
        } else {
            User existUser = userService.getUserByUserName(user.getUserName());
            String message = existUser == null ? "该用户名尚未注册" : "密码错误";
            result.put("success", false);
            result.put("message", message);
        }
        
        return result;
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Integer userId) {
        return userService.queryById(userId);
    }

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{userName}")
    public User getUserByUserName(@PathVariable String userName) {
        return userService.getUserByUserName(userName);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Map<String, Object> updateUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userService.update(user);
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        return result;
    }

    /**
     * 管理员登录（开发测试用）
     */
    @GetMapping("/admin/login")
    public Map<String, Object> adminLogin() {
        Map<String, Object> result = new HashMap<>();
        User user = userService.createDefaultAdmin();
        result.put("success", true);
        result.put("user", user);
        result.put("message", "管理员登录成功");
        return result;
    }
} 