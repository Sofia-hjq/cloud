package com.moti.common.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MyFile实体类单元测试
 *
 * @author 莫提
 */
class MyFileTest {

    private MyFile file;

    @BeforeEach
    void setUp() {
        file = MyFile.builder()
                .myFileId(1)
                .myFileName("测试文件.txt")
                .fileStoreId(1)
                .myFilePath("/uploads/test.txt")
                .downloadTime(0)
                .uploadTime(new Date())
                .parentFolderId(0)
                .size(1024)
                .type(1)
                .postfix("txt")
                .build();
    }

    @Test
    void testMyFileBuilder() {
        assertNotNull(file);
        assertEquals(1, file.getMyFileId());
        assertEquals("测试文件.txt", file.getMyFileName());
        assertEquals(1, file.getFileStoreId());
        assertEquals("/uploads/test.txt", file.getMyFilePath());
        assertEquals(0, file.getDownloadTime());
        assertNotNull(file.getUploadTime());
        assertEquals(0, file.getParentFolderId());
        assertEquals(1024, file.getSize());
        assertEquals(1, file.getType());
        assertEquals("txt", file.getPostfix());
    }

    @Test
    void testDefaultConstructor() {
        MyFile emptyFile = new MyFile();
        assertNotNull(emptyFile);
        assertNull(emptyFile.getMyFileId());
        assertNull(emptyFile.getMyFileName());
    }

    @Test
    void testSettersAndGetters() {
        MyFile testFile = new MyFile();
        
        testFile.setMyFileId(2);
        testFile.setMyFileName("新文件.pdf");
        testFile.setFileStoreId(2);
        testFile.setMyFilePath("/uploads/new.pdf");
        testFile.setDownloadTime(5);
        Date uploadTime = new Date();
        testFile.setUploadTime(uploadTime);
        testFile.setParentFolderId(1);
        testFile.setSize(2048);
        testFile.setType(2);
        testFile.setPostfix("pdf");

        assertEquals(2, testFile.getMyFileId());
        assertEquals("新文件.pdf", testFile.getMyFileName());
        assertEquals(2, testFile.getFileStoreId());
        assertEquals("/uploads/new.pdf", testFile.getMyFilePath());
        assertEquals(5, testFile.getDownloadTime());
        assertEquals(uploadTime, testFile.getUploadTime());
        assertEquals(1, testFile.getParentFolderId());
        assertEquals(2048, testFile.getSize());
        assertEquals(2, testFile.getType());
        assertEquals("pdf", testFile.getPostfix());
    }

    @Test
    void testEqualsAndHashCode() {
        MyFile file1 = MyFile.builder()
                .myFileId(1)
                .myFileName("test.txt")
                .build();

        MyFile file2 = MyFile.builder()
                .myFileId(1)
                .myFileName("test.txt")
                .build();

        assertEquals(file1, file2);
        assertEquals(file1.hashCode(), file2.hashCode());
    }

    @Test
    void testToString() {
        String fileString = file.toString();
        assertNotNull(fileString);
        assertTrue(fileString.contains("测试文件.txt"));
        assertTrue(fileString.contains("MyFile"));
    }

    @Test
    void testDifferentFileTypes() {
        // 测试不同文件类型
        MyFile imageFile = MyFile.builder()
                .myFileName("image.jpg")
                .type(2)
                .postfix("jpg")
                .build();

        MyFile videoFile = MyFile.builder()
                .myFileName("video.mp4")
                .type(3)
                .postfix("mp4")
                .build();

        MyFile docFile = MyFile.builder()
                .myFileName("document.docx")
                .type(4)
                .postfix("docx")
                .build();

        assertEquals("jpg", imageFile.getPostfix());
        assertEquals(2, imageFile.getType());
        
        assertEquals("mp4", videoFile.getPostfix());
        assertEquals(3, videoFile.getType());
        
        assertEquals("docx", docFile.getPostfix());
        assertEquals(4, docFile.getType());
    }

    @Test
    void testFileSize() {
        // 测试不同大小的文件
        MyFile smallFile = MyFile.builder()
                .size(1024)  // 1KB
                .build();

        MyFile mediumFile = MyFile.builder()
                .size(1048576)  // 1MB
                .build();

        MyFile largeFile = MyFile.builder()
                .size(1073741824)  // 1GB
                .build();

        assertEquals(1024, smallFile.getSize());
        assertEquals(1048576, mediumFile.getSize());
        assertEquals(1073741824, largeFile.getSize());
    }
} 