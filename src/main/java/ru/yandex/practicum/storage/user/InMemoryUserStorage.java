package ru.yandex.practicum.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private Long nextUserId = 1L;

    @Override
    public User create(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("У пользователя поле nickname и login одинаковый, т.к. поле nickname было пустое");
            user = user.toBuilder().name(user.getLogin()).build();
        }
        //назначаем id пользователю
        user = user.toBuilder().id(nextUserId++).build();
        log.debug("Пользователь: {}, успешно создан", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("У пользователя поле nickname и login одинаковый, т.к. поле nickname было пустое");
            user = user.toBuilder().name(user.getLogin()).build();
        }
        log.debug("Пользователь: {}, успешно обновлен", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void delete(Long userId) {
        checkUser(userId);
        users.remove(userId);
    }

    @Override
    public User findUserById(Long userId) {
        checkUser(userId);
        return users.get(userId);
    }

    private void validate(User user) {
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("У пользователя поле login пустое или содержит пробел");
            throw new ValidationException("Логин не должен быть пустым и при этом не должен содержать пробелы");
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("У пользователя поле email пустое или не содержит '@'");
            throw new ValidationException("Поле с email не может быть пустым или email должен содержать '@'");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("У пользователя поле dateOfBirth не правильно определено");
            throw new ValidationException("День рождения не может быть в будущем");
        }
    }

    private void checkUser(Long userId) {
        if (userId < 0 || !users.containsKey(userId)) {
            log.warn("Пользователь под id " + userId + " не найден");
            throw new NotFoundException("Пользователь под id " + userId + " не найден");
        }
    }
}
