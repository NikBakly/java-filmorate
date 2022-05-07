package ru.yandex.practicum.storage.film;

import ru.yandex.practicum.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void create(Film film);

    void update(Film film);

    Film findFilmById(Long filmId);

    Collection<Film> findALl();

}
