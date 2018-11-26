package com.mine.boot.service.impl;

import com.mine.boot.dao.UserMapper;
import com.mine.boot.pojo.User;
import com.mine.boot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl
 *
 * @author zhangsl
 * @date 2018/11/26 15:02
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }
}
