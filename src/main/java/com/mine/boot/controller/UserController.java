package com.mine.boot.controller;

import com.mine.boot.pojo.User;
import com.mine.boot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable(value = "id", required = true) Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/user")
    public String addUser(@RequestBody User user) {
        userService.addUser(user);
        return "success";
    }

    @PutMapping("/user")
    public User updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return user;
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable(value = "id", required = true) Long id) {
        userService.deleteUser(id);
    }
}
