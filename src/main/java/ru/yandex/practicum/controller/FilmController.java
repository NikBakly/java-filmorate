package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    //Create film
    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    //Update film
    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }

    //get film
    @GetMapping("/films")
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/films/{id}")
    public Film findFilmById(@PathVariable("id") Long filmId) {
        return filmService.findFilmById(filmId);
    }

    @DeleteMapping("/films/{id}")
    public void delete(@PathVariable("id") Long filmId) {
        filmService.delete(filmId);
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
