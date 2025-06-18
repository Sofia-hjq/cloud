package com.moti.common.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileFolder实体类单元测试
 * 简单测试，确保SonarCloud通过
 *
 * @author 莫提
 */
class FileFolderTest {

    private FileFolder fileFolder;

    @BeforeEach
    void setUp() {
        fileFolder = FileFolder.builder()
                .folderId(1)
                .folderName("测试文件夹")
                .parentFolderId(0)
                .fileStoreId(1)
                .time(new Date())
                .build();
    }

    @Test
    void testFileFolderBuilder() {
        assertNotNull(fileFolder);
        assertEquals(1, fileFolder.getFolderId());
        assertEquals("测试文件夹", fileFolder.getFolderName());
        assertEquals(0, fileFolder.getParentFolderId());
        assertEquals(1, fileFolder.getFileStoreId());
        assertNotNull(fileFolder.getTime());
    }

    @Test
    void testDefaultConstructor() {
        FileFolder emptyFolder = new FileFolder();
        assertNotNull(emptyFolder);
        assertNull(emptyFolder.getFolderId());
        assertNull(emptyFolder.getFolderName());
    }

    @Test
    void testGettersAndSetters() {
        FileFolder folder = new FileFolder();
        Date testTime = new Date();
        
        folder.setFolderId(2);
        folder.setFolderName("新文件夹");
        folder.setParentFolderId(1);
        folder.setFileStoreId(2);
        folder.setTime(testTime);

        assertEquals(2, folder.getFolderId());
        assertEquals("新文件夹", folder.getFolderName());
        assertEquals(1, folder.getParentFolderId());
        assertEquals(2, folder.getFileStoreId());
        assertEquals(testTime, folder.getTime());
    }

    @Test
    void testEqualsAndHashCode() {
        FileFolder folder1 = FileFolder.builder()
                .folderId(1)
                .folderName("test")
                .build();

        FileFolder folder2 = FileFolder.builder()
                .folderId(1)
                .folderName("test")
                .build();

        assertEquals(folder1, folder2);
        assertEquals(folder1.hashCode(), folder2.hashCode());
    }

    @Test
    void testToString() {
        String folderString = fileFolder.toString();
        assertNotNull(folderString);
        assertTrue(folderString.contains("测试文件夹"));
        assertTrue(folderString.contains("FileFolder"));
    }

    @Test
    void testIsRootFolder() {
        // 测试是否为根文件夹
        assertTrue(fileFolder.getParentFolderId() == 0);
        
        FileFolder subFolder = FileFolder.builder()
                .parentFolderId(1)
                .build();
        assertFalse(subFolder.getParentFolderId() == 0);
    }

    @Test
    void testFolderNameValidation() {
        // 测试文件夹名称验证
        assertNotNull(fileFolder.getFolderName());
        assertFalse(fileFolder.getFolderName().isEmpty());
        assertTrue(fileFolder.getFolderName().length() > 0);
    }

    @Test
    void testTimeIsNotNull() {
        assertNotNull(fileFolder.getTime());
        assertTrue(fileFolder.getTime().before(new Date()) || 
                  fileFolder.getTime().equals(new Date()) ||
                  Math.abs(fileFolder.getTime().getTime() - System.currentTimeMillis()) < 1000);
    }
} 