package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    //Add to friends
    public void addToFriend(Long userId, Long friendId) {
        validateCheckUser(userId);
        validateCheckUser(friendId);
        //добавления друзей для двоих
        userStorage
                .findUserById(userId)
                .getFriends()
                .put(friendId, Boolean.TRUE);
        userStorage
                .findUserById(friendId)
                .getFriends()
                .put(userId, Boolean.FALSE);
        log.debug("Добавление в друзья прошло успешно у пользователей id = {} и id = {}", userId, friendId);

    }

    //Delete from friends
    public void deleteFriend(Long userId, Long friendId) {
        validateCheckUser(userId);
        validateCheckUser(friendId);
        //удаление друзей для двоих
        userStorage.findUserById(userId).getFriends().remove(friendId);
        userStorage.findUserById(friendId).getFriends().remove(userId);
        log.debug("Удаление из друзей прошло успешно у пользователей id = {} и id = {}", userId, friendId);
    }

    //Get mutual friends
    public List<User> getMutualFriends(Long userId, Long friendId) {
        validateCheckUser(userId);
        validateCheckUser(friendId);
        //друзья пользователя
        Set<Long> friendsUser = userStorage.findUserById(userId).getFriends().keySet();
        //друзья друга
        Set<Long> friendsFriend = userStorage.findUserById(friendId).getFriends().keySet();
        //список общих друзей
        List<User> mutualFriends = new ArrayList<>();

        for (Long id : friendsUser) {
            if (!id.equals(friendId) && friendsFriend.contains(id)) {
                mutualFriends.add(userStorage.findUserById(id));
            }
        }
        log.debug("Общие друзья вернулись успешно у пользователей id = {} и id = {}", userId, friendId);
        return mutualFriends;
    }

    public List<User> getFriends(Long userId) {
        validateCheckUser(userId);
        List<User> friends = new ArrayList<>();
        for (Long idFriend : userStorage.findUserById(userId).getFriends().keySet()) {
            friends.add(userStorage.findUserById(idFriend));
        }
        log.debug("Все друзья вернулись успешно у пользователя id = {}", userId);
        return friends;
    }

    private void validateCheckUser(Long userId) {
        if (userStorage.findUserById(userId) == null) {
            log.warn("Пользователь под id = " + userId + " не найден");
            throw new NotFoundException("Пользователь под id = " + userId + " не найден");
        }
    }

}
