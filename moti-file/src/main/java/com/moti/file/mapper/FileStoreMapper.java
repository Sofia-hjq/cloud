package com.moti.file.mapper;

import com.moti.common.entity.FileStore;
import org.apache.ibatis.annotations.*;

/**
 * @ClassName: FileStoreMapper
 * @Description: 文件仓库映射接口
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Mapper
public interface FileStoreMapper {
    
    @Insert("INSERT INTO file_store(user_id, current_size, max_size, permission) " +
            "VALUES(#{userId}, #{currentSize}, #{maxSize}, #{permission})")
    @Options(useGeneratedKeys = true, keyProperty = "fileStoreId")
    int insert(FileStore fileStore);
    
    @Select("SELECT * FROM file_store WHERE file_store_id = #{fileStoreId}")
    FileStore selectById(Integer fileStoreId);
    
    @Select("SELECT * FROM file_store WHERE user_id = #{userId}")
    FileStore selectByUserId(Integer userId);
    
    @Update("UPDATE file_store SET current_size = current_size + #{sizeChange} WHERE user_id = #{userId}")
    int updateSize(@Param("userId") Integer userId, @Param("sizeChange") Integer sizeChange);
    
    @Update("UPDATE file_store SET permission = #{permission} WHERE user_id = #{userId}")
    int updatePermission(@Param("userId") Integer userId, @Param("permission") Integer permission);
} 