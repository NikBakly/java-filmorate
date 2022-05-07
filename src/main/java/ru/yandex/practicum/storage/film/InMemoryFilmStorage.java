package ru.yandex.practicum.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public void create( Film film) {
        validate(film);
        log.debug("Фильм: {}, успешно создан", film);
        films.put(film.getId(), film);
    }

    @Override
    public void update( Film film) {
        validate(film);
        log.debug("Фильм: {}, успешно обновлен", film);
        films.put(film.getId(), film);
    }

    @Override
    public Collection<Film> findALl() {
        return films.values();
    }

    @Override
    public Film findFilmById(Long filmId) {
        if (filmId < 0 || !films.containsKey(filmId)) {
            log.warn("Фильм под id " + filmId + " не найден");
            throw new NotFoundException("Фильм под id " + filmId + " не найден");
        }
        return films.get(filmId);
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
