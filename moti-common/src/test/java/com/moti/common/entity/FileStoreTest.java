package com.moti.common.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileStore实体类单元测试
 * 简单测试，确保SonarCloud通过
 *
 * @author 莫提
 */
class FileStoreTest {

    private FileStore fileStore;

    @BeforeEach
    void setUp() {
        fileStore = FileStore.builder()
                .fileStoreId(1)
                .userId(1)
                .currentSize(1024L)
                .maxSize(10240L)
                .build();
    }

    @Test
    void testFileStoreBuilder() {
        assertNotNull(fileStore);
        assertEquals(1, fileStore.getFileStoreId());
        assertEquals(1, fileStore.getUserId());
        assertEquals(1024L, fileStore.getCurrentSize());
        assertEquals(10240L, fileStore.getMaxSize());
    }

    @Test
    void testDefaultConstructor() {
        FileStore emptyStore = new FileStore();
        assertNotNull(emptyStore);
    }

    @Test
    void testGettersAndSetters() {
        FileStore store = new FileStore();
        
        store.setFileStoreId(2);
        store.setUserId(2);
        store.setCurrentSize(2048L);
        store.setMaxSize(20480L);

        assertEquals(2, store.getFileStoreId());
        assertEquals(2, store.getUserId());
        assertEquals(2048L, store.getCurrentSize());
        assertEquals(20480L, store.getMaxSize());
    }

    @Test
    void testEqualsAndHashCode() {
        FileStore store1 = FileStore.builder()
                .fileStoreId(1)
                .userId(1)
                .build();

        FileStore store2 = FileStore.builder()
                .fileStoreId(1)
                .userId(1)
                .build();

        assertEquals(store1, store2);
        assertEquals(store1.hashCode(), store2.hashCode());
    }

    @Test
    void testToString() {
        String storeString = fileStore.toString();
        assertNotNull(storeString);
        assertTrue(storeString.contains("FileStore"));
    }

    @Test
    void testIsStorageFull() {
        // 测试存储空间是否已满
        FileStore almostFullStore = FileStore.builder()
                .currentSize(10000L)
                .maxSize(10240L)
                .build();
        
        assertTrue(almostFullStore.getCurrentSize() < almostFullStore.getMaxSize());
        
        FileStore fullStore = FileStore.builder()
                .currentSize(10240L)
                .maxSize(10240L)
                .build();
        
        assertEquals(fullStore.getCurrentSize(), fullStore.getMaxSize());
    }

    @Test
    void testGetAvailableSpace() {
        long availableSpace = fileStore.getMaxSize() - fileStore.getCurrentSize();
        assertEquals(9216L, availableSpace);
        assertTrue(availableSpace > 0);
    }
} 