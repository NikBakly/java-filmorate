package ru.yandex.practicum.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.IdFilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final IdFilmService idFilmService;

    @Autowired
    public InMemoryFilmStorage(IdFilmService idFilmService) {
        this.idFilmService = idFilmService;
    }

    @Override
    public Film create(Film film) {
        validate(film);
        //назначаем id фильму
        film = film.toBuilder().id(idFilmService.getNextFilmId()).build();
        log.debug("Фильм: {}, успешно создан", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        checkFilm(film.getId());
        validate(film);
        log.debug("Фильм: {}, успешно обновлен", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(Long filmId) {
        checkFilm(filmId);
        films.remove(filmId);
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

    private void checkFilm(Long filmId){
        if (filmId < 0 || !films.containsKey(filmId)){
            log.warn("Фильм id " + filmId + " не найден");
            throw new NotFoundException("Фильм id " + filmId + " не найден");
        }
    }

    private void validate(@Valid Film film) {
        if (film.getName().isBlank()) {
            log.warn("У фильма поле name пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200 || film.getDescription().isBlank()) {
            log.warn("У фильма поле description содержит более 200 символов");
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 11, 28))) {
            log.warn("У фильма поле realeseDate раньше даты 28 декабря 1985");
            throw new ValidationException("Дата релиза не должна быть раньше чем 28 декабря 1985");
        }
        if (film.getDuration() <= 0) {
            log.warn("У фильма поле duration не является положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
