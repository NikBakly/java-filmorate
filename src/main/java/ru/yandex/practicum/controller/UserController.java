package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Map<String, User> users = new HashMap<>();

    //Create user
    @PostMapping("/users")
    public void create(@RequestBody User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("У пользователя поле nickname и login одинаковый, т.к. поле nickname было пустое");
            user = user.toBuilder().name(user.getLogin()).build();
        }
        log.debug("Пользователь: {}, успешно создан", user);
        users.put(user.getLogin(), user);
    }

    //Update user
    @PutMapping("/users")
    public void update(@RequestBody User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("У пользователя поле nickname и login одинаковый, т.к. поле nickname было пустое");
            user = user.toBuilder().name(user.getLogin()).build();
        }
        log.debug("Пользователь: {}, успешно обновлен", user);
        users.put(user.getLogin(), user);
    }

    //get user
    @GetMapping("/users")
    public Collection<User> findAll() {
        return users.values();
    }

    private void validate(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("У пользователя поле email пустое или не содержит '@'");
            throw new ValidationException("Поле с email не может быть пустым или email должен содержать '@'");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("У пользователя поле login пустое или содержит пробел");
            throw new ValidationException("Логин не должен быть пустым и при этом не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("У пользователя поле dateOfBirth не правильно определено");
            throw new ValidationException("День рождения не может быть в будущем");
        }
    }
}
