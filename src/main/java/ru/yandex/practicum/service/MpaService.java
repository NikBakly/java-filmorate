package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.MpaNotFoundException;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    //получения mpa рейтинга по его id
    public Mpa getById(int mpaId) throws MpaNotFoundException {
        final Mpa mpa = mpaStorage.getMpaById(mpaId);
        if (mpa == null) {
            throw new MpaNotFoundException("Mpa id = " + mpaId + " не найден");
        }
        return mpa;
    }

    //получения списка значений mpa рейтинга
    public List<Mpa> getAll() {
        return mpaStorage.getAllMpa();
    }
}
