package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;

@Service
public class IdFilmService {
    private Long nextFilmId = 1L;

    public Long getNextFilmId() {
        return nextFilmId++;
    }
}
