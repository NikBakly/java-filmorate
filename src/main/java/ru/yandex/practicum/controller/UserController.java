package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;


    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.userService = new UserService(userStorage);
    }

    //Create user
    @PostMapping("/users")
    public User create(@RequestBody User user) {
        if (user.getFriends() == null) {
            //Создаем множество друзей напрямую
            user = user.toBuilder().friends(new HashMap<>()).build();
        }
        //Назначаем id пользователю
        return userStorage.create(user);
    }

    //Update user
    @PutMapping("/users")
    public User update(@RequestBody User user) {
        if (user.getFriends() == null) {
            //Создаем множество друзей напрямую
            user = user.toBuilder().friends(new HashMap<>()).build();
        }
        return userStorage.update(user);
    }

    //Get all users
    @GetMapping("/users")
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable("id") Long userId) {
        return userStorage.findUserById(userId);
    }

    @DeleteMapping("/uders/{id}")
    public void delete(@PathVariable("id") Long userId) {
        userStorage.delete(userId);
    }

    //Adding to friends
    @PutMapping("/users/{id}/friends/{friendId}")
    public void addToFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        userService.addToFriend(userId, friendId);
    }

    //Removing from friends
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    //Get list friends
    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Long userId) {
        return userService.getFriends(userId);
    }

    //Get mutual friends
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("id") Long userId, @PathVariable("otherId") Long friendId) {
        return userService.getMutualFriends(userId, friendId);
    }

}
