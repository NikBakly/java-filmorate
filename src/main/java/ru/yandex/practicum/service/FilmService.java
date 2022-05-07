package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    private final Set<Long> appreciatedUsers = new HashSet<>();

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Long filmId, Long userId) {
        validateCheckFilm(filmId);
        //Проверка, что пользователь не оценил фильм
        if (appreciatedUsers.contains(userId)) {
            log.warn("Пользователь под id = " + userId + " уже ставил лайк фильму");
            throw new ValidationException("Пользователь под id = " + userId + " уже ставил лайк фильму");
        }
        //Увеличиваем оценку фильма на 1
        addFilmLike(filmId);
        //Записываем пользователя в оценившие
        appreciatedUsers.add(userId);

        log.debug("Лайк фильму id = {} добавлен успешно", filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        validateCheckFilm(filmId);
        //Проверка, что пользователь не оценил фильм
        if (!appreciatedUsers.contains(userId)) {
            log.warn("Пользователь под id = " + userId + " не ставил лайк фильму");
            throw new NotFoundException("Пользователь под id = " + userId + " не ставил лайк фильму");
        }
        //Получаем оценки фильма
        Long filmRate = getFilmRate(filmId);
        if (filmRate <= 0) {
            log.warn("У фильма id = " + filmId + " оценка не может быть отрицательной");
            throw new ValidationException("У фильма id = " + filmId + " оценка не может быть отрицательной");
        }
        //удаляем лайк у фильма
        deleteFilmLike(filmId);
        //удаляем пользователя из оценивших
        appreciatedUsers.remove(userId);
        log.debug("Лайк пользователя id = {} успешно удален у фильма id = {}", userId, filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> popularFilms = new ArrayList<>(filmStorage.findALl());
        //Сортировка по количеству лайков на убывание
        popularFilms.sort((o1, o2) -> (int) (o2.getRate() - o1.getRate()));

        if (count != null && popularFilms.size() >= count) {
            return popularFilms.subList(0, count);
        }
        log.debug("Популярные фильмы в количестве: {} успешно вернулись", count);
        return popularFilms.subList(0, popularFilms.size());
    }

    //Возвращает оценку фильму
    private Long getFilmRate(Long filmId) {
        return filmStorage
                .findFilmById(filmId)
                .getRate();
    }

    //добавляет оценку фильму на один
    private void addFilmLike(Long filmId) {
        filmStorage
                .findFilmById(filmId)
                .addRate();
    }

    //удаляет одну оценку фильму
    private void deleteFilmLike(Long filmId) {
        filmStorage
                .findFilmById(filmId)
                .deleteRate();
    }

    //проверка фильма на существование
    private void validateCheckFilm(Long filmId) {
        if (filmStorage.findFilmById(filmId) == null) {
            log.warn("Фильм под id = " + filmId + " не найден");
            throw new NotFoundException("Фильм под id = " + filmId + " не найден");
        }
    }
}
