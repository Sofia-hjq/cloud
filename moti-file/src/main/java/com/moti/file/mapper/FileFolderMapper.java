package com.moti.file.mapper;

import com.moti.common.entity.FileFolder;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @ClassName: FileFolderMapper
 * @Description: 文件夹映射接口
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Mapper
public interface FileFolderMapper {
    
    @Insert("INSERT INTO file_folder(file_folder_name, parent_folder_id, file_store_id, time) " +
            "VALUES(#{fileFolderName}, #{parentFolderId}, #{fileStoreId}, #{time})")
    @Options(useGeneratedKeys = true, keyProperty = "fileFolderId")
    int insert(FileFolder fileFolder);
    
    @Select("SELECT * FROM file_folder WHERE file_folder_id = #{folderId}")
    FileFolder selectById(Integer folderId);
    
    @Select("SELECT ff.* FROM file_folder ff " +
            "JOIN file_store fs ON ff.file_store_id = fs.file_store_id " +
            "WHERE fs.user_id = #{userId} AND ff.parent_folder_id = #{parentFolderId}")
    List<FileFolder> selectByUserIdAndParent(@Param("userId") Integer userId, 
                                           @Param("parentFolderId") Integer parentFolderId);
    
    @Select("SELECT ff.* FROM file_folder ff " +
            "JOIN file_store fs ON ff.file_store_id = fs.file_store_id " +
            "WHERE fs.user_id = #{userId}")
    List<FileFolder> selectAllByUserId(Integer userId);
    
    @Select("SELECT COUNT(*) FROM file_folder ff " +
            "JOIN file_store fs ON ff.file_store_id = fs.file_store_id " +
            "WHERE fs.user_id = #{userId}")
    int countByUserId(Integer userId);
    
    @Update("UPDATE file_folder SET file_folder_name = #{fileFolderName} WHERE file_folder_id = #{fileFolderId}")
    int updateById(FileFolder fileFolder);
    
    @Delete("DELETE FROM file_folder WHERE file_folder_id = #{folderId}")
    int deleteById(Integer folderId);
} 