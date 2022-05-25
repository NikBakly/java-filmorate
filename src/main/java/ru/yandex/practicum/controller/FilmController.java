package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;


    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        this.filmService = new FilmService(filmStorage);
    }

    //Create film
    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        if (film.getNumberOfLikes() == null) {
            film = film.toBuilder().numberOfLikes(0L).build();
        }
        return filmStorage.create(film);
    }

    //Update film
    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        if (film.getNumberOfLikes() == null) {
            film = film.toBuilder().numberOfLikes(0L).build();
        }
        return filmStorage.update(film);
    }

    //get film
    @GetMapping("/films")
    public Collection<Film> findAll() {
        return filmStorage.findALl();
    }

    @GetMapping("/films/{id}")
    public Film findFilmById(@PathVariable("id") Long filmId) {
        return filmStorage.findFilmById(filmId);
    }

    @DeleteMapping("/films/{id}")
    public void delete(@PathVariable("id") Long filmId) {
        filmStorage.delete(filmId);
    }

    //User likes the movie
    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    //User deletes the like
    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        filmService.deleteLike(filmId, userId);
    }

    //Get popular movies
    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }
}
