package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    private Long userId = 1L;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.userService = new UserService(userStorage);
    }

    //Create user
    @PostMapping("/users")
    public void create(@RequestBody User user) {
        if (user.getId() == null) {
            //Назначение пользователю id программно
            user = user.toBuilder().id(userId++).build();
        }
        if (user.getFriends() == null) {
            user = user.toBuilder().friends(new HashSet<>()).build();
        }
        userStorage.create(user);
    }

    //Update user
    @PutMapping("/users")
    public void update(@RequestBody User user) {
        userStorage.update(user);
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
