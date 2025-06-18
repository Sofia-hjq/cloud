package com.moti.file.service;

import com.moti.common.entity.MyFile;
import com.moti.common.entity.FileStore;
import com.moti.file.mapper.FileMapper;
import com.moti.file.mapper.FileStoreMapper;
import com.moti.file.service.impl.FileServiceImpl;
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
 * FileService单元测试
 *
 * @author 莫提
 */
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileMapper fileMapper;

    @Mock
    private FileStoreMapper fileStoreMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    private MyFile testFile;

    @BeforeEach
    void setUp() {
        testFile = MyFile.builder()
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
    void testDownloadFile() {
        // Given
        when(fileMapper.selectById(1)).thenReturn(testFile);
        when(fileMapper.updateById(any(MyFile.class))).thenReturn(1);

        // When
        MyFile result = fileService.downloadFile(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getMyFileId());
        assertEquals("测试文件.txt", result.getMyFileName());
        verify(fileMapper, times(1)).selectById(1);
        verify(fileMapper, times(1)).updateById(any(MyFile.class));
    }

    @Test
    void testDownloadFileNotFound() {
        // Given
        when(fileMapper.selectById(999)).thenReturn(null);

        // When
        MyFile result = fileService.downloadFile(999);

        // Then
        assertNull(result);
        verify(fileMapper, times(1)).selectById(999);
    }

    @Test
    void testGetUserFiles() {
        // Given
        List<MyFile> fileList = Arrays.asList(testFile, 
            MyFile.builder().myFileId(2).myFileName("文件2.pdf").build());
        when(fileMapper.selectByUserIdAndFolder(1, 0)).thenReturn(fileList);

        // When
        List<MyFile> result = fileService.getUserFiles(1, 0);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(fileMapper, times(1)).selectByUserIdAndFolder(1, 0);
    }

    @Test
    void testGetAllUserFiles() {
        // Given
        List<MyFile> fileList = Arrays.asList(testFile);
        when(fileMapper.selectAllByUserId(1)).thenReturn(fileList);

        // When
        List<MyFile> result = fileService.getAllUserFiles(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fileMapper, times(1)).selectAllByUserId(1);
    }

    @Test
    void testGetFilesByType() {
        // Given
        List<MyFile> fileList = Arrays.asList(testFile);
        when(fileMapper.selectByUserIdAndType(1, 1)).thenReturn(fileList);

        // When
        List<MyFile> result = fileService.getFilesByType(1, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fileMapper, times(1)).selectByUserIdAndType(1, 1);
    }

    @Test
    void testSearchFiles() {
        // Given
        List<MyFile> fileList = Arrays.asList(testFile);
        when(fileMapper.searchByKeyword(1, "测试")).thenReturn(fileList);

        // When
        List<MyFile> result = fileService.searchFiles(1, "测试");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fileMapper, times(1)).searchByKeyword(1, "测试");
    }

    @Test
    void testDeleteFile() {
        // Given
        FileStore fileStore = FileStore.builder()
                .fileStoreId(1)
                .userId(1)
                .build();
        when(fileMapper.selectById(1)).thenReturn(testFile);
        when(fileStoreMapper.selectById(1)).thenReturn(fileStore);
        when(fileMapper.deleteById(1)).thenReturn(1);
        when(fileStoreMapper.updateSize(1, -1024)).thenReturn(1);

        // When
        boolean result = fileService.deleteFile(1, 1);

        // Then
        assertTrue(result);
        verify(fileMapper, times(1)).selectById(1);
        verify(fileStoreMapper, times(1)).selectById(1);
        verify(fileMapper, times(1)).deleteById(1);
    }

    @Test
    void testCreateFileStore() {
        // Given
        when(fileStoreMapper.insert(any(FileStore.class))).thenReturn(1);

        // When
        FileStore result = fileService.createFileStore(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(0, result.getCurrentSize());
        verify(fileStoreMapper, times(1)).insert(any(FileStore.class));
    }

    @Test
    void testGetUserFileStore() {
        // Given
        FileStore fileStore = FileStore.builder()
                .fileStoreId(1)
                .userId(1)
                .build();
        when(fileStoreMapper.selectByUserId(1)).thenReturn(fileStore);

        // When
        FileStore result = fileService.getUserFileStore(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        verify(fileStoreMapper, times(1)).selectByUserId(1);
    }

    @Test
    void testUpdateFileStoreSize() {
        // Given
        when(fileStoreMapper.updateSize(1, 1024)).thenReturn(1);

        // When
        boolean result = fileService.updateFileStoreSize(1, 1024);

        // Then
        assertTrue(result);
        verify(fileStoreMapper, times(1)).updateSize(1, 1024);
    }
} 