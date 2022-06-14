package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.exception.GenreNotFoundException;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    //получения жанра по его id
    @GetMapping("/{genreId}")
    public Genre getById(@PathVariable int genreId) throws GenreNotFoundException {
        log.debug("Входящий запрос на получение жанра по id = {}", genreId);
        return genreService.getById(genreId);
    }

    //получения списка всех жанров
    @GetMapping
    public List<Genre> getAll() {
        log.debug("Входящий запрос на получение списка всех жанров");
        return genreService.getAll();
    }
}
