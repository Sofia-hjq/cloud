package com.moti.user.mapper;

import com.moti.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @InterfaceName: UserMapper
 * @Description: 用户数据库操作
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
@Mapper
public interface UserMapper {

    /**
     * 添加用户
     */
    int insert(User user);
    
    /**
     * 删除用户
     */
    int deleteById(@Param("userId") Integer userId);

    /**
     * 通过ID查询单条数据
     */
    User queryById(@Param("userId") Integer userId);

    /**
     * 通过用户名和密码查询单条数据
     */
    User queryByUserNameAndPwd(@Param("userName") String userName, @Param("password") String password);

    /**
     * 通过用户名查询单条数据
     */
    User getUserByUserName(@Param("userName") String userName);

    /**
     * 查询全部数据
     */
    List<User> queryAll();

    /**
     * 实体作为筛选条件查询数据
     */
    List<User> queryByCondition(User user);

    /**
     * 修改用户
     */
    int update(User user);
    
    /**
     * 为用户创建文件仓库
     */
    int createFileStore(@Param("userId") Integer userId);
    
    /**
     * 更新用户的文件仓库ID
     */
    int updateUserFileStoreId(@Param("userId") Integer userId);
} 