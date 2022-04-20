package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private final Map<LocalDate, Film> films = new HashMap<>();

    //Create film
    @PostMapping("/films")
    public void create(@RequestBody Film film) {
        validate(film);
        log.debug("Фильм: {}, успешно создан", film);
        films.put(film.getReleaseDate(), film);
    }

    //Update film
    @PutMapping("/films")
    public void update(@RequestBody Film film) {
        validate(film);
        log.debug("Фильм: {}, успешно обновлен", film);
        films.put(film.getReleaseDate(), film);
    }

    //get film
    @GetMapping("/films")
    public Collection<Film> findAll() {
        return films.values();
    }

    private void validate(@Valid Film film) {
        if (film.getName().isBlank()) {
            log.warn("У фильма поле name пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("У фильма поле description содержит более 200 символов");
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 11, 28))) {
            log.warn("У фильма поле realeseDate раньше даты 28 декабря 1985");
            throw new ValidationException("Дата релиза не должна быть раньше чем 28 декабря 1985");
        }
        if (film.getDuration().isNegative() || film.getDuration().getSeconds() == 0) {
            log.warn("У фильма поле duration не является положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
