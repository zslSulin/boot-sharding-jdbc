package com.mine.boot.service.impl;

import com.mine.boot.dao.UserMapper;
import com.mine.boot.pojo.User;
import com.mine.boot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl
 *
 * @author zhangsl
 * @date 2018/12/13 14:06
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public Long addUser(User user) {
        userMapper.insert(user);
        log.info("最新user的id: {}", user.getId());
        return user.getId();
    }
}
