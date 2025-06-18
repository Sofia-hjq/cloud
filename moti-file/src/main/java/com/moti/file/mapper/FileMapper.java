package com.moti.file.mapper;

import com.moti.common.entity.MyFile;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @ClassName: FileMapper
 * @Description: 文件映射接口
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Mapper
public interface FileMapper {
    
    @Insert("INSERT INTO my_file(my_file_name, file_store_id, my_file_path, download_time, " +
            "upload_time, parent_folder_id, size, type, postfix) " +
            "VALUES(#{myFileName}, #{fileStoreId}, #{myFilePath}, #{downloadTime}, " +
            "#{uploadTime}, #{parentFolderId}, #{size}, #{type}, #{postfix})")
    @Options(useGeneratedKeys = true, keyProperty = "myFileId")
    int insert(MyFile myFile);
    
    @Select("SELECT * FROM my_file WHERE my_file_id = #{fileId}")
    MyFile selectById(Integer fileId);
    
    @Update("UPDATE my_file SET my_file_name=#{myFileName}, download_time=#{downloadTime} " +
            "WHERE my_file_id=#{myFileId}")
    int updateById(MyFile myFile);
    
    @Delete("DELETE FROM my_file WHERE my_file_id = #{fileId}")
    int deleteById(Integer fileId);
    
    @Select("SELECT f.* FROM my_file f " +
            "JOIN file_store fs ON f.file_store_id = fs.file_store_id " +
            "WHERE fs.user_id = #{userId} AND f.parent_folder_id = #{parentFolderId}")
    List<MyFile> selectByUserIdAndFolder(@Param("userId") Integer userId, 
                                        @Param("parentFolderId") Integer parentFolderId);
    
    @Select("SELECT f.* FROM my_file f " +
            "JOIN file_store fs ON f.file_store_id = fs.file_store_id " +
            "WHERE fs.user_id = #{userId}")
    List<MyFile> selectAllByUserId(Integer userId);
    
    @Select("SELECT f.* FROM my_file f " +
            "JOIN file_store fs ON f.file_store_id = fs.file_store_id " +
            "WHERE fs.user_id = #{userId} AND f.type = #{type}")
    List<MyFile> selectByUserIdAndType(@Param("userId") Integer userId, 
                                      @Param("type") Integer type);
    
    @Select("SELECT f.* FROM my_file f " +
            "JOIN file_store fs ON f.file_store_id = fs.file_store_id " +
            "WHERE fs.user_id = #{userId} AND f.my_file_name LIKE CONCAT('%', #{keyword}, '%')")
    List<MyFile> searchByKeyword(@Param("userId") Integer userId, 
                                @Param("keyword") String keyword);
} 