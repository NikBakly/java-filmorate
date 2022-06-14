package ru.yandex.practicum.storage.genre;

import ru.yandex.practicum.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getGenreById(int id);

    List<Genre> getAllGenres();
}

