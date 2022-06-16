package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.storage.filmLikes.FilmLikesStorage;
import ru.yandex.practicum.storage.film.FilmStorage;
import ru.yandex.practicum.storage.genre.GenreStorage;
import ru.yandex.practicum.storage.user.UserStorage;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikesStorage filmLikesStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       FilmLikesStorage filmLikesStorage,
                       FilmGenreStorage filmGenreStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.filmLikesStorage = filmLikesStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.genreStorage = genreStorage;
    }

    public Film create(Film film) {
        Film filmWithId = filmStorage.create(film);
        film.setId(filmWithId.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenreStorage.create(film.getId(), genre.getId());
            }
        }
        return film;
    }

    public Film update(Film film) {
        validateCheckFilm(film.getId());
        filmGenreStorage.deleteByFilmId(film.getId());
        filmStorage.update(film);
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenreStorage.create(film.getId(), genre.getId());
            }
        }
        return film;
    }

    public Film findFilmById(Long filmId) {
        final Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Film сid = " + filmId + " не найден");
        } else {
            TreeSet<Integer> genresId = filmGenreStorage.getByFilmId(filmId);
            LinkedHashSet<Genre> genres = new LinkedHashSet<>();
            if (!genresId.isEmpty()) {
                for (Integer genreId : genresId) {
                    genres.add(genreStorage.getGenreById(genreId));
                }
                film.setGenres(genres);
            } else {
                film.setGenres(null);
            }
        }
        return film;
    }

    public void delete(Long filmId) {
        filmStorage.deleteById(filmId);
    }

    public Collection<Film> findAll() {
        return filmStorage.findALl();
    }

    public void addLike(Long filmId, Long userId) {
        validateCheckUser(userId);
        validateCheckFilm(filmId);
        filmLikesStorage.addLike(filmId, userId);
        log.debug("Лайк фильму id = {} добавлен успешно", filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        validateCheckUser(userId);
        validateCheckFilm(filmId);
        filmLikesStorage.deleteLike(filmId, userId);
        log.debug("Лайк пользователя id = {} успешно удален у фильма id = {}", userId, filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> popularFilms = filmLikesStorage.findPopularFilms(count);
        log.debug("Популярные фильмы в количестве: {} успешно вернулись", count);
        return popularFilms.subList(0, popularFilms.size());
    }

    //проверка фильма на существование
    private void validateCheckFilm(Long filmId) {
        if (filmStorage.findFilmById(filmId) == null) {
            log.warn("Фильм под id = " + filmId + " не найден");
            throw new NotFoundException("Фильм под id = " + filmId + " не найден");
        }
    }

    private void validateCheckUser(Long userId) {
        if (userId < 0) {
            log.warn("Пользователь под id " + userId + " не найден");
            throw new NotFoundException("Пользователь под id " + userId + " не найден");
        }
    }
}
