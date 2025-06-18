package com.moti.user.service.impl;

import com.moti.common.entity.User;
import com.moti.user.mapper.UserMapper;
import com.moti.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description 用户服务实现类
 * @author moti
 * @date 2023-12-09
 * @Version 1.0
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean register(User user) {
        try {
            // 先插入用户
            boolean result = insert(user);
            if (result && user.getUserId() != null) {
                // 注册成功后，创建用户的文件仓库
                createFileStoreForUser(user.getUserId());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 为用户创建文件仓库
     */
    private void createFileStoreForUser(Integer userId) {
        try {
            // 直接在数据库中插入文件仓库记录
            userMapper.createFileStore(userId);
            // 更新用户的file_store_id
            userMapper.updateUserFileStoreId(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User login(String userName, String password) {
        return userMapper.queryByUserNameAndPwd(userName, password);
    }

    @Override
    public boolean insert(User user) {
        return userMapper.insert(user) == 1;
    }

    @Override
    public boolean deleteById(Integer userId) {
        return userMapper.deleteById(userId) == 1;
    }

    @Override
    public User queryById(Integer userId) {
        return userMapper.queryById(userId);
    }

    @Override
    public User getUserByUserName(String userName) {
        return userMapper.getUserByUserName(userName);
    }

    @Override
    public User queryByUserNameAndPwd(String userName, String password) {
        return userMapper.queryByUserNameAndPwd(userName, password);
    }

    @Override
    public List<User> queryAll() {
        return userMapper.queryAll();
    }

    @Override
    public List<User> queryByCondition(User user) {
        return userMapper.queryByCondition(user);
    }

    @Override
    public boolean update(User user) {
        return userMapper.update(user) == 1;
    }

    @Override
    public User createDefaultAdmin() {
        User user = queryById(1);
        if (user == null) {
            // 如果没有管理员用户，创建一个默认的
            user = User.builder()
                    .userName("admin")
                    .password("admin123")
                    .imagePath(null) // 不再需要头像图片路径，使用首字符显示
                    .registerTime(new Date())
                    .role(0) // 管理员角色
                    .build();
            insert(user);
        }
        return user;
    }
} 