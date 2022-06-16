package ru.yandex.practicum.storage.filmLikes;

import ru.yandex.practicum.model.Film;

import java.util.List;

public interface FilmLikesStorage {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> findPopularFilms(int count);
}
