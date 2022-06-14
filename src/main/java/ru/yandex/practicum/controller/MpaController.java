package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.exception.MpaNotFoundException;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.service.MpaService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    //получения mpa рейтинга по его id
    @GetMapping("/mpa/{mpaId}")
    public Mpa getById(@PathVariable int mpaId) throws MpaNotFoundException {
        log.debug("Входящий запрос на получение mpa рейтинга по id = {}", mpaId);
        return mpaService.getById(mpaId);
    }

    //получения всех значений рейтинга mpa
    @GetMapping("/mpa")
    public List<Mpa> getAll() {
        log.debug("Входящий запрос на получение списка всего рейтинга");
        return mpaService.getAll();
    }
}
