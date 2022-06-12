package ru.yandex.practicum.storage.friendship;

import ru.yandex.practicum.model.User;

import java.util.List;

public interface FriendshipStorage {
    void addToFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getMutualFriends(Long userId, Long friendId);

    List<User> getFriends(Long userId);
}
