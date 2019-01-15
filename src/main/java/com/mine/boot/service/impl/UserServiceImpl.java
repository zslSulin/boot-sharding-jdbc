package com.mine.boot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mine.boot.mapper.UserMapper;
import com.mine.boot.pojo.User;
import com.mine.boot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
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
    @Cacheable(key = "'user'.concat(#id.toString())")
    public User getUserById(Long id) {
        User user = userMapper.selectByPrimaryKey(id);
        redisTemplate.opsForHash().put("user_" + id, "age", user.getAge());
        redisTemplate.opsForHash().put("user_" + id, "loginName", user.getLoginName());
        redisTemplate.opsForHash().put("user_" + id, "sex", user.getSex());
        redisTemplate.opsForValue().set("user_obj" + id, user);
        redisTemplate.opsForValue().set("user_str" + id, JSONObject.toJSONString(user));
        return user;
    }

    @Override
    public Long addUser(User user) {
        userMapper.insert(user);
        log.info("最新user的id: {}", user.getId());
        return user.getId();
    }
}
