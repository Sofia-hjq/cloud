package com.moti.file.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: PageController
 * @Description: 页面控制器，处理前端路由和用户认证
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Controller
public class PageController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 首页
     */
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    /**
     * 登录成功后处理
     */
    @PostMapping("/api/login")
    public String handleLogin(@RequestParam String userName, 
                             @RequestParam String password, 
                             Model model, 
                             HttpServletRequest request) {
        try {
            // 构建用户数据
            Map<String, Object> userData = new HashMap<>();
            userData.put("userName", userName);
            userData.put("password", password);

            // 调用用户服务登录接口
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://moti-user/user/login", 
                entity, 
                Map.class
            );

            Map<String, Object> result = response.getBody();
            
            if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                // 登录成功，设置session并跳转
                Map<String, Object> user = (Map<String, Object>) result.get("user");
                
                // 确保用户对象有所需的属性
                if (user.get("imagePath") == null) {
                    user.put("imagePath", "/static/img/default-avatar.png");
                }
                
                request.getSession().setAttribute("currentUser", user);
                return "redirect:/u-admin";
            } else {
                // 登录失败，返回首页并显示错误信息
                String errorMsg = result != null ? (String) result.get("message") : "登录失败";
                model.addAttribute("errorMsg", errorMsg);
                return "index";
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", "系统异常，登录失败：" + e.getMessage());
            return "index";
        }
    }

    /**
     * 用户登出
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/";
    }

    /**
     * 管理员页面
     */
    @GetMapping("/admin")
    public String admin() {
        return "admin/manage-users";
    }

    /**
     * 临时文件页面
     */
    @GetMapping("/temp-file")
    public String tempFile() {
        return "temp-file";
    }

    /**
     * 设置用户session（AJAX登录后调用）
     */
    @PostMapping("/api/set-session")
    @ResponseBody
    public Map<String, Object> setUserSession(@RequestBody Map<String, Object> userData, 
                                             HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证用户数据
            if (userData == null || userData.get("userName") == null) {
                result.put("success", false);
                result.put("message", "用户数据无效");
                return result;
            }
            
            // 确保用户对象有所需的属性
            if (userData.get("imagePath") == null) {
                userData.put("imagePath", "/static/img/default-avatar.png");
            }
            
            // 设置session
            request.getSession().setAttribute("currentUser", userData);
            
            result.put("success", true);
            result.put("message", "Session设置成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "设置Session失败：" + e.getMessage());
        }
        
        return result;
    }
    

} 