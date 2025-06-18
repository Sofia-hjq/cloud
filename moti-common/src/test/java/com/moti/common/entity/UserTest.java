package com.moti.common.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User实体类单元测试
 *
 * @author 莫提
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1)
                .userName("测试用户")
                .password("password123")
                .openId("test_openid")
                .fileStoreId(1)
                .registerTime(new Date())
                .imagePath("/images/avatar.jpg")
                .role(1)
                .build();
    }

    @Test
    void testUserBuilder() {
        assertNotNull(user);
        assertEquals(1, user.getUserId());
        assertEquals("测试用户", user.getUserName());
        assertEquals("password123", user.getPassword());
        assertEquals("test_openid", user.getOpenId());
        assertEquals(1, user.getFileStoreId());
        assertEquals("/images/avatar.jpg", user.getImagePath());
        assertEquals(1, user.getRole());
    }

    @Test
    void testGetAvatarCharWithChineseName() {
        // Given
        user.setUserName("张三");

        // When
        String avatarChar = user.getAvatarChar();

        // Then
        assertEquals("张", avatarChar);
    }

    @Test
    void testGetAvatarCharWithEnglishName() {
        // Given
        user.setUserName("john");

        // When
        String avatarChar = user.getAvatarChar();

        // Then
        assertEquals("J", avatarChar);
    }

    @Test
    void testGetAvatarCharWithNullName() {
        // Given
        user.setUserName(null);

        // When
        String avatarChar = user.getAvatarChar();

        // Then
        assertEquals("U", avatarChar);
    }

    @Test
    void testGetAvatarCharWithEmptyName() {
        // Given
        user.setUserName("   ");

        // When
        String avatarChar = user.getAvatarChar();

        // Then
        assertEquals("U", avatarChar);
    }

    @Test
    void testGetAvatarColorWithName() {
        // Given
        user.setUserName("测试用户");

        // When
        String avatarColor = user.getAvatarColor();

        // Then
        assertNotNull(avatarColor);
        assertTrue(avatarColor.startsWith("avatar-color-"));
        assertTrue(avatarColor.matches("avatar-color-[1-8]"));
    }

    @Test
    void testGetAvatarColorWithNullName() {
        // Given
        user.setUserName(null);

        // When
        String avatarColor = user.getAvatarColor();

        // Then
        assertEquals("avatar-color-1", avatarColor);
    }

    @Test
    void testGetAvatarColorWithEmptyName() {
        // Given
        user.setUserName("  ");

        // When
        String avatarColor = user.getAvatarColor();

        // Then
        assertEquals("avatar-color-1", avatarColor);
    }

    @Test
    void testGetAvatarColorConsistency() {
        // Given
        String userName = "测试用户";
        user.setUserName(userName);

        // When
        String color1 = user.getAvatarColor();
        String color2 = user.getAvatarColor();

        // Then
        assertEquals(color1, color2, "同一用户名应该返回相同的颜色");
    }

    @Test
    void testSettersAndGetters() {
        // Test setters
        user.setUserId(2);
        user.setUserName("新用户名");
        user.setPassword("newPassword");
        user.setOpenId("new_openid");
        user.setFileStoreId(2);
        user.setImagePath("/new/path.jpg");
        user.setRole(0);

        // Test getters
        assertEquals(2, user.getUserId());
        assertEquals("新用户名", user.getUserName());
        assertEquals("newPassword", user.getPassword());
        assertEquals("new_openid", user.getOpenId());
        assertEquals(2, user.getFileStoreId());
        assertEquals("/new/path.jpg", user.getImagePath());
        assertEquals(0, user.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
                .userId(1)
                .userName("test")
                .build();

        User user2 = User.builder()
                .userId(1)
                .userName("test")
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        String userString = user.toString();
        assertNotNull(userString);
        assertTrue(userString.contains("测试用户"));
        assertTrue(userString.contains("User"));
    }
} 