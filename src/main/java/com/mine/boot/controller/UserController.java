package com.mine.boot.controller;

import com.mine.boot.pojo.User;
import com.mine.boot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 *
 * @author zhangsl
 * @date 2018/11/26 14:57
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserById")
    public User getUserById(Long id) {
        User user = userService.getUserById(id);
        return user;
    }
}
