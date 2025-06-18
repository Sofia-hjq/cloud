package com.moti.user.service;

import com.moti.common.entity.User;
import java.util.List;

/**
 * @InterfaceName: UserService
 * @Description: 用户业务层接口
 * @author: moti
 * @date 2023/12/9
 * @Version: 1.0
 **/
public interface UserService {

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 用户登录
     */
    User login(String userName, String password);

    /**
     * 添加用户
     */
    boolean insert(User user);

    /**
     * 删除用户
     */
    boolean deleteById(Integer userId);

    /**
     * 查询单条数据
     */
    User queryById(Integer userId);

    /**
     * 通过用户名查询单条数据
     */
    User getUserByUserName(String userName);

    /**
     * 通过用户名和密码查询单条数据
     */
    User queryByUserNameAndPwd(String userName, String password);

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
    boolean update(User user);

    /**
     * 创建默认管理员
     */
    User createDefaultAdmin();
} 