package com.mine.boot.service.impl;

import com.mine.boot.mapper.UserMapper;
import com.mine.boot.pojo.User;
import com.mine.boot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl
 *
 * @author zhangsl
 * @date 2018/12/13 14:06
 */
@CacheConfig(cacheNames = "user")
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Cacheable(key = "'user'.concat(#id.toString())", unless = "#result == null")
    public User getUserById(Long id) {
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    @Override
    @CachePut(key = "'user'.concat(#user.id.toString())", unless = "#result == null ")
    public User addUser(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    @CachePut(key = "'user'.concat(#user.id.toString())", condition = "#user.id != null || #user.id != 0L")
    public User updateUser(User user) {
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }

    @Override
    @CacheEvict(key = "'user'.concat(#id.toString())")
    public void deleteUser(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }
}
