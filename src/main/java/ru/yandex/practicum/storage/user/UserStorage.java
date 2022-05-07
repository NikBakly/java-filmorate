package ru.yandex.practicum.storage.user;

import ru.yandex.practicum.model.User;

import java.util.Collection;

public interface UserStorage {
    void create(User user);

    void update(User user);

    User findUserById(Long userId);

    Collection<User> findAll();
}
