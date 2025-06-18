package com.moti.file.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.common.entity.MyFile;
import com.moti.file.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FileController单元测试
 *
 * @author 莫提
 */
@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testUploadFile() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()
        );
        when(fileService.uploadFile(any(), eq(1), eq(0))).thenReturn(testFile);

        // When & Then
        mockMvc.perform(multipart("/api/file/upload")
                .file(file)
                .param("userId", "1")
                .param("parentFolderId", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.myFileName").value("测试文件.txt"));

        verify(fileService, times(1)).uploadFile(any(), eq(1), eq(0));
    }

    @Test
    void testGetUserFiles() throws Exception {
        // Given
        List<MyFile> fileList = Arrays.asList(testFile);
        when(fileService.getUserFiles(1, 0)).thenReturn(fileList);

        // When & Then
        mockMvc.perform(get("/api/file/list")
                .param("userId", "1")
                .param("parentFolderId", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].myFileName").value("测试文件.txt"));

        verify(fileService, times(1)).getUserFiles(1, 0);
    }

    @Test
    void testDownloadFile() throws Exception {
        // Given
        when(fileService.downloadFile(1)).thenReturn(testFile);

        // When & Then
        mockMvc.perform(get("/api/file/download/1"))
                .andExpect(status().isOk());

        verify(fileService, times(1)).downloadFile(1);
    }

    @Test
    void testDeleteFile() throws Exception {
        // Given
        when(fileService.deleteFile(1, 1)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/file/delete/1")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("删除成功"));

        verify(fileService, times(1)).deleteFile(1, 1);
    }

    @Test
    void testDeleteFileFailed() throws Exception {
        // Given
        when(fileService.deleteFile(1, 1)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/file/delete/1")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败"));
    }

    @Test
    void testGetFilesByType() throws Exception {
        // Given
        List<MyFile> fileList = Arrays.asList(testFile);
        when(fileService.getFilesByType(1, 1)).thenReturn(fileList);

        // When & Then
        mockMvc.perform(get("/api/file/type/1")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(fileService, times(1)).getFilesByType(1, 1);
    }

    @Test
    void testSearchFiles() throws Exception {
        // Given
        List<MyFile> fileList = Arrays.asList(testFile);
        when(fileService.searchFiles(1, "测试")).thenReturn(fileList);

        // When & Then
        mockMvc.perform(get("/api/file/search")
                .param("userId", "1")
                .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(fileService, times(1)).searchFiles(1, "测试");
    }

    @Test
    void testUpdateFileName() throws Exception {
        // Given
        when(fileService.updateFileName(any(MyFile.class), eq(1))).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/file/rename")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFile))
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("重命名成功"));

        verify(fileService, times(1)).updateFileName(any(MyFile.class), eq(1));
    }
} 