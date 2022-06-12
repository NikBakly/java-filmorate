package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmLikes.FilmLikesStorage;
import ru.yandex.practicum.storage.film.FilmStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikesStorage filmLikesStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmLikesStorage filmLikesStorage) {
        this.filmStorage = filmStorage;
        this.filmLikesStorage = filmLikesStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film findFilmById(Long filmId) {
        return filmStorage.findFilmById(filmId);
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
        if(userId < 0){
            log.warn("Пользователь под id " + userId + " не найден");
            throw new NotFoundException("Пользователь под id " + userId + " не найден");
        }
    }
}
