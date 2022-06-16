package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.GenreNotFoundException;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    // получения жанра по его id
    public Genre getById(int genreId) throws GenreNotFoundException {
        final Genre genre = genreStorage.getGenreById(genreId);
        if (genre == null) {
            throw new GenreNotFoundException("Genre с id = " + genreId + " не найден");
        }
        return genre;
    }

    // получения списка всех жанров
    public List<Genre> getAll() {
        return genreStorage.getAllGenres();
    }
}
