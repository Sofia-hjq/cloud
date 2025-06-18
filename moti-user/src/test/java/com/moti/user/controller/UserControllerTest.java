package com.moti.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.common.entity.User;
import com.moti.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController单元测试
 *
 * @author 莫提
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1)
                .userName("测试用户")
                .password("password123")
                .registerTime(new Date())
                .role(1)
                .fileStoreId(1)
                .build();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        // Given
        when(userService.getUserByUserName(anyString())).thenReturn(null);
        when(userService.register(any(User.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册成功"));

        verify(userService, times(1)).getUserByUserName("测试用户");
        verify(userService, times(1)).register(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() throws Exception {
        // Given
        when(userService.getUserByUserName(anyString())).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("该用户名已被注册"));

        verify(userService, times(1)).getUserByUserName("测试用户");
        verify(userService, never()).register(any(User.class));
    }

    @Test
    void testRegisterFailed() throws Exception {
        // Given
        when(userService.getUserByUserName(anyString())).thenReturn(null);
        when(userService.register(any(User.class))).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("注册失败"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Given
        when(userService.login(anyString(), anyString())).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.user.userId").value(1));

        verify(userService, times(1)).login("测试用户", "password123");
    }

    @Test
    void testLoginFailedUserNotExists() throws Exception {
        // Given
        when(userService.login(anyString(), anyString())).thenReturn(null);
        when(userService.getUserByUserName(anyString())).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("该用户名尚未注册"));
    }

    @Test
    void testLoginFailedWrongPassword() throws Exception {
        // Given
        when(userService.login(anyString(), anyString())).thenReturn(null);
        when(userService.getUserByUserName(anyString())).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }

    @Test
    void testGetUserById() throws Exception {
        // Given
        when(userService.queryById(1)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.userName").value("测试用户"));

        verify(userService, times(1)).queryById(1);
    }

    @Test
    void testGetUserByUserName() throws Exception {
        // Given
        when(userService.getUserByUserName("测试用户")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/user/username/测试用户"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.userName").value("测试用户"));

        verify(userService, times(1)).getUserByUserName("测试用户");
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        // Given
        when(userService.update(any(User.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新成功"));

        verify(userService, times(1)).update(any(User.class));
    }

    @Test
    void testUpdateUserFailed() throws Exception {
        // Given
        when(userService.update(any(User.class))).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("更新失败"));
    }

    @Test
    void testAdminLogin() throws Exception {
        // Given
        User adminUser = User.builder()
                .userId(1)
                .userName("admin")
                .role(0)
                .build();
        when(userService.createDefaultAdmin()).thenReturn(adminUser);

        // When & Then
        mockMvc.perform(get("/user/admin/login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("管理员登录成功"))
                .andExpect(jsonPath("$.user.userName").value("admin"));

        verify(userService, times(1)).createDefaultAdmin();
    }
} 