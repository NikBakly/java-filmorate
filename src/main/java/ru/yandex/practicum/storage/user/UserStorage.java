package ru.yandex.practicum.storage.user;

import ru.yandex.practicum.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(Long userId);

    User findUserById(Long userId);

    Collection<User> findAll();

}
