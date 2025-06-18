package com.moti.user.service;

import com.moti.common.entity.User;
import com.moti.user.mapper.UserMapper;
import com.moti.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService单元测试
 *
 * @author 莫提
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1)
                .userName("测试用户")
                .openId("test_openid_123")
                .fileStoreId(1)
                .password("password123")
                .registerTime(new Date())
                .imagePath("/images/avatar.jpg")
                .role(1)
                .build();
    }

    @Test
    void testQueryById() {
        // Given
        when(userMapper.queryById(1)).thenReturn(testUser);

        // When
        User result = userService.queryById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("测试用户", result.getUserName());
        verify(userMapper, times(1)).queryById(1);
    }

    @Test
    void testQueryByIdNotFound() {
        // Given
        when(userMapper.queryById(999)).thenReturn(null);

        // When
        User result = userService.queryById(999);

        // Then
        assertNull(result);
        verify(userMapper, times(1)).queryById(999);
    }

    @Test
    void testGetUserByUserName() {
        // Given
        when(userMapper.getUserByUserName("测试用户")).thenReturn(testUser);

        // When
        User result = userService.getUserByUserName("测试用户");

        // Then
        assertNotNull(result);
        assertEquals("测试用户", result.getUserName());
        verify(userMapper, times(1)).getUserByUserName("测试用户");
    }

    @Test
    void testGetUserByUserNameNotFound() {
        // Given
        when(userMapper.getUserByUserName("nonexistent")).thenReturn(null);

        // When
        User result = userService.getUserByUserName("nonexistent");

        // Then
        assertNull(result);
        verify(userMapper, times(1)).getUserByUserName("nonexistent");
    }

    @Test
    void testInsert() {
        // Given
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.insert(testUser);

        // Then
        assertTrue(result);
        verify(userMapper, times(1)).insert(testUser);
    }

    @Test
    void testUpdate() {
        // Given
        when(userMapper.update(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.update(testUser);

        // Then
        assertTrue(result);
        verify(userMapper, times(1)).update(testUser);
    }

    @Test
    void testQueryAll() {
        // Given
        List<User> userList = Arrays.asList(testUser, 
            User.builder().userId(2).userName("用户2").build());
        when(userMapper.queryAll()).thenReturn(userList);

        // When
        List<User> result = userService.queryAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userMapper, times(1)).queryAll();
    }

    @Test
    void testDeleteById() {
        // Given
        when(userMapper.deleteById(1)).thenReturn(1);

        // When
        boolean result = userService.deleteById(1);

        // Then
        assertTrue(result);
        verify(userMapper, times(1)).deleteById(1);
    }

    @Test
    void testLogin() {
        // Given
        when(userMapper.queryByUserNameAndPwd("测试用户", "password123")).thenReturn(testUser);

        // When
        User result = userService.login("测试用户", "password123");

        // Then
        assertNotNull(result);
        assertEquals("测试用户", result.getUserName());
        verify(userMapper, times(1)).queryByUserNameAndPwd("测试用户", "password123");
    }

    @Test
    void testQueryByUserNameAndPwd() {
        // Given
        when(userMapper.queryByUserNameAndPwd("测试用户", "password123")).thenReturn(testUser);

        // When
        User result = userService.queryByUserNameAndPwd("测试用户", "password123");

        // Then
        assertNotNull(result);
        assertEquals("测试用户", result.getUserName());
        verify(userMapper, times(1)).queryByUserNameAndPwd("测试用户", "password123");
    }

    @Test
    void testRegister() {
        // Given
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.register(testUser);

        // Then
        assertTrue(result);
        verify(userMapper, times(1)).insert(testUser);
    }

    @Test
    void testCreateDefaultAdmin() {
        // Given
        when(userMapper.queryById(1)).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // When
        User result = userService.createDefaultAdmin();

        // Then
        assertNotNull(result);
        assertEquals("admin", result.getUserName());
        assertEquals(0, result.getRole());
        verify(userMapper, times(1)).queryById(1);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testCreateDefaultAdminExists() {
        // Given
        User adminUser = User.builder()
                .userId(1)
                .userName("admin")
                .role(0)
                .build();
        when(userMapper.queryById(1)).thenReturn(adminUser);

        // When
        User result = userService.createDefaultAdmin();

        // Then
        assertNotNull(result);
        assertEquals("admin", result.getUserName());
        assertEquals(0, result.getRole());
        verify(userMapper, times(1)).queryById(1);
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testQueryByCondition() {
        // Given
        User condition = User.builder().role(1).build();
        List<User> userList = Arrays.asList(testUser);
        when(userMapper.queryByCondition(condition)).thenReturn(userList);

        // When
        List<User> result = userService.queryByCondition(condition);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userMapper, times(1)).queryByCondition(condition);
    }
} 