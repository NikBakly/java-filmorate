package ru.yandex.practicum.storage.filmGenre;

import java.util.TreeSet;

public interface FilmGenreStorage {
    void create(Long filmId, Long genreId);

    TreeSet<Integer> getByFilmId(Long filmId);

    void deleteByFilmId(Long filmId);
}
