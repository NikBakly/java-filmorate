package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;

@Service
public class IdUserService {
    private Long nextUserId = 1L;

    public Long getNextUserId() {
        return nextUserId++;
    }
}
