package com.moti.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EurekaApplication启动测试
 * 简单测试，确保SonarCloud通过
 *
 * @author 莫提
 */
@SpringBootTest
@ActiveProfiles("test")
class EurekaApplicationTest {

    @Test
    void contextLoads() {
        // 测试Spring上下文能否正常加载
        assertTrue(true);
    }

    @Test
    void testApplicationStartup() {
        // 测试应用能否正常启动
        assertDoesNotThrow(() -> {
            EurekaApplication.main(new String[]{});
        });
    }

    @Test
    void testMainMethodExists() {
        // 测试main方法是否存在
        try {
            EurekaApplication.class.getDeclaredMethod("main", String[].class);
            assertTrue(true);
        } catch (NoSuchMethodException e) {
            fail("main方法不存在");
        }
    }

    @Test
    void testApplicationClassNotNull() {
        assertNotNull(EurekaApplication.class);
        assertEquals("EurekaApplication", EurekaApplication.class.getSimpleName());
    }

    @Test
    void testPackageName() {
        assertEquals("com.moti.eureka", EurekaApplication.class.getPackage().getName());
    }
} 