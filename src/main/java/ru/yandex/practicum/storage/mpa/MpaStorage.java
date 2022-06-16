package ru.yandex.practicum.storage.mpa;

import ru.yandex.practicum.model.Mpa;

import java.util.List;

public interface MpaStorage {
    Mpa getMpaById(int id);

    List<Mpa> getAllMpa();
}
