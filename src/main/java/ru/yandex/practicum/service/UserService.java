package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    public User findUserById(Long userId) {
        return userStorage.findUserById(userId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    //Add to friends
    public void addToFriend(Long userId, Long friendId) {
        friendshipStorage.addToFriend(userId, friendId);
        log.debug("Пользователь id = {} успешно добавил в друзья другого пользователя id = {}", userId, friendId);
    }

    //Delete from friends
    public void deleteFriend(Long userId, Long friendId) {
        friendshipStorage.deleteFriend(userId, friendId);
        log.debug("Пользователь id = {} успешно удалил из друзей другого пользователя id = {}", userId, friendId);
    }

    //Get mutual friends
    public List<User> getMutualFriends(Long userId, Long friendId) {
        List<User> mutualFriends = friendshipStorage.getMutualFriends(userId, friendId);
        log.debug("Общие друзья вернулись успешно у пользователей id = {} и id = {}", userId, friendId);
        return mutualFriends;
    }

    public List<User> getFriends(Long userId) {
        List<User> friends = friendshipStorage.getFriends(userId);
        log.debug("Все друзья вернулись успешно у пользователя id = {}", userId);
        return friends;
    }
}
