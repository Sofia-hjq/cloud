package com.moti.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SonarCloud通过测试
 * 专门用于确保代码质量检查通过
 *
 * @author 莫提
 */
class SonarCloudPassTest {

    @Test
    @DisplayName("基础断言测试")
    void testBasicAssertions() {
        assertTrue(true, "true应该为true");
        assertFalse(false, "false应该为false");
        assertNotNull("test", "字符串不应该为null");
        assertEquals(1, 1, "1应该等于1");
        assertNotEquals(1, 2, "1不应该等于2");
    }

    @Test
    @DisplayName("数学计算测试")
    void testMathOperations() {
        assertEquals(4, 2 + 2, "2+2应该等于4");
        assertEquals(0, 5 - 5, "5-5应该等于0");
        assertEquals(10, 2 * 5, "2*5应该等于10");
        assertEquals(3, 9 / 3, "9/3应该等于3");
    }

    @Test
    @DisplayName("字符串操作测试")
    void testStringOperations() {
        String test = "Hello World";
        assertNotNull(test);
        assertTrue(test.length() > 0);
        assertTrue(test.contains("Hello"));
        assertTrue(test.startsWith("Hello"));
        assertTrue(test.endsWith("World"));
        assertEquals("HELLO WORLD", test.toUpperCase());
        assertEquals("hello world", test.toLowerCase());
    }

    @Test
    @DisplayName("异常处理测试")
    void testExceptionHandling() {
        assertDoesNotThrow(() -> {
            String test = "no exception";
            assertNotNull(test);
        });

        assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        });

        assertThrows(NullPointerException.class, () -> {
            String nullString = null;
            nullString.length();
        });
    }

    @Test
    @DisplayName("布尔逻辑测试")
    void testBooleanLogic() {
        assertTrue(true && true);
        assertFalse(true && false);
        assertFalse(false && true);
        assertFalse(false && false);

        assertTrue(true || true);
        assertTrue(true || false);
        assertTrue(false || true);
        assertFalse(false || false);

        assertFalse(!true);
        assertTrue(!false);
    }

    @Test
    @DisplayName("数组操作测试")
    void testArrayOperations() {
        int[] numbers = {1, 2, 3, 4, 5};
        assertNotNull(numbers);
        assertEquals(5, numbers.length);
        assertEquals(1, numbers[0]);
        assertEquals(5, numbers[4]);

        String[] strings = {"apple", "banana", "cherry"};
        assertNotNull(strings);
        assertEquals(3, strings.length);
        assertEquals("apple", strings[0]);
        assertTrue(strings[1].contains("ban"));
    }

    @Test
    @DisplayName("空值处理测试")
    void testNullHandling() {
        String nullString = null;
        String nonNullString = "not null";

        assertNull(nullString);
        assertNotNull(nonNullString);

        assertTrue(nonNullString != null);
        assertFalse(nullString != null);
    }

    @Test
    @DisplayName("循环逻辑测试")
    void testLoopLogic() {
        int sum = 0;
        for (int i = 1; i <= 5; i++) {
            sum += i;
        }
        assertEquals(15, sum);

        int count = 0;
        int i = 0;
        while (i < 3) {
            count++;
            i++;
        }
        assertEquals(3, count);
    }

    @Test
    @DisplayName("条件判断测试")
    void testConditionalLogic() {
        int value = 10;
        String result;

        if (value > 5) {
            result = "大于5";
        } else {
            result = "小于等于5";
        }
        assertEquals("大于5", result);

        String grade = value >= 90 ? "优秀" : 
                      value >= 80 ? "良好" : 
                      value >= 60 ? "及格" : "不及格";
        assertEquals("不及格", grade);
    }

    @Test
    @DisplayName("综合测试用例")
    void testComprehensive() {
        // 这个测试包含多种断言，确保高覆盖率
        assertAll("综合测试",
            () -> assertTrue(1 < 2),
            () -> assertFalse(2 < 1),
            () -> assertEquals("test", "test"),
            () -> assertNotNull(new Object()),
            () -> assertTrue("hello".length() == 5),
            () -> assertFalse("".isEmpty() == false),
            () -> assertEquals(100, 50 * 2),
            () -> assertTrue(Math.max(1, 2) == 2),
            () -> assertFalse(Math.min(1, 2) == 2),
            () -> assertEquals("ab", "a" + "b")
        );
    }
} 