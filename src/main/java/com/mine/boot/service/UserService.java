package com.mine.boot.service;

import com.mine.boot.pojo.User;

/**
 * UserService
 *
 * @author zhangsl
 * @date 2018/12/13 14:05
 */
public interface UserService {
    User getUserById(Long id);

    Long addUser(User user);
}
