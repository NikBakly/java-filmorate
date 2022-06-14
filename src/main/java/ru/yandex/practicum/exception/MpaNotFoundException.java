package ru.yandex.practicum.exception;

public class MpaNotFoundException extends RuntimeException {
    public MpaNotFoundException(String message) {
        super(message);
    }
}

