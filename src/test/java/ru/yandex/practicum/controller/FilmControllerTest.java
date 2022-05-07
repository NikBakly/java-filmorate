package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.film.FilmStorage;
import ru.yandex.practicum.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.storage.user.UserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class FilmControllerTest {
    private final FilmStorage filmStorage = new InMemoryFilmStorage();

    private FilmController controllerFilm;
    private Film guardian;

    @BeforeEach
    void init() {
        controllerFilm = new FilmController(filmStorage);
        guardian = Film.builder()
                .id(1L)
                .name("guardian")
                .description("Бывший агент элитных спецслужб спасает девочку")
                .duration(Duration.ofMinutes(91))
                .releaseDate(LocalDate.of(2012, 4, 26))
                .build();
        controllerFilm.create(guardian);

    }

    @Test
    void test1_shouldThrowExceptionWithEmptyName() {
        Film film = Film.builder()
                .id(1L)
                .name("")
                .description("very cute")
                .releaseDate(LocalDate.of(2001, 12, 23))
                .duration(Duration.ofMinutes(93))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerFilm.create(film));

        Assertions.assertEquals("Название фильма не может быть пустым", thrown.getMessage());
    }

    @Test
    void test2_shouldThrowExceptionIfDescriptionMore200Characters() {
        Film titanic = Film.builder()
                .id(1L)
                .name("Titanic")
                .description("В первом и последнем плавании шикарного «Титаника» встречаются двое." +
                        " Пассажир нижней палубы Джек выиграл билет в карты," +
                        " а богатая наследница Роза отправляется в Америку, чтобы выйти замуж по расчёту." +
                        " Чувства молодых людей только успевают расцвести, и даже не классовые различия создадут" +
                        " испытания влюблённым, а айсберг, вставший на пути считавшегося непотопляемым лайнера.")
                .releaseDate(LocalDate.of(1998, 2, 20))
                .duration(Duration.ofMinutes(134))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerFilm.create(titanic));

        Assertions.assertEquals("Описание фильма не может превышать 200 символов", thrown.getMessage());
    }

    @Test
    void test3_shouldThrowExceptionIfReleaseDateBefore28December1985() {
        Film film = Film.builder()
                .id(1L)
                .name("test")
                .description("Test")
                .releaseDate(LocalDate.of(1890, 2, 20))
                .duration(Duration.ofMinutes(50))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerFilm.create(film));

        Assertions.assertEquals("Дата релиза не должна быть раньше чем 28 декабря 1985", thrown.getMessage());
    }

    @Test
    void test4_shouldThrowExceptionIfDurationIsNegative() {
        Film film = Film.builder()
                .id(1L)
                .name("test")
                .description("Test")
                .releaseDate(LocalDate.of(1900, 2, 20))
                .duration(Duration.ofMinutes(-10))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerFilm.create(film));

        Assertions.assertEquals("Продолжительность фильма должна быть положительной", thrown.getMessage());
    }

    @Test
    void test5_shouldThrowExceptionIfDurationIsZero() {
        Film film = Film.builder()
                .id(1L)
                .name("test")
                .description("Test")
                .releaseDate(LocalDate.of(1900, 2, 20))
                .duration(Duration.ofMinutes(0))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerFilm.create(film));

        Assertions.assertEquals("Продолжительность фильма должна быть положительной", thrown.getMessage());
    }

    @Test
    void test6_shouldSuccessfullySaveFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("film")
                .description("film")
                .releaseDate(LocalDate.of(1999, 2, 20))
                .duration(Duration.ofMinutes(60))
                .build();

        controllerFilm.create(film);

        Assertions.assertTrue(controllerFilm.findAll().contains(film));
    }

    @Test
    void test7_shouldAddLike() {
        controllerFilm.addLike(guardian.getId(), 1L);

        Assertions.assertEquals(1L, guardian.getRate());
    }

    @Test
    void test8_shouldDeleteLike() {
        controllerFilm.addLike(guardian.getId(), 1L);
        controllerFilm.addLike(guardian.getId(), 2L);

        controllerFilm.deleteLike(guardian.getId(), 1L);

        Assertions.assertEquals(1L, guardian.getRate());
    }

    @Test
    void test9_shouldGetPopularFilms() {
        Film testFilm = Film.builder()
                .id(2L)
                .name("test")
                .description("very cute test")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(Duration.ofMinutes(25))
                .build();
        controllerFilm.create(testFilm);

        controllerFilm.addLike(guardian.getId(), 1L);
        controllerFilm.addLike(guardian.getId(), 2L);

        controllerFilm.addLike(testFilm.getId(), 3L);

        List<Film> popularFilms = controllerFilm.getPopularFilms(2);

        List<Film> expectedPopularFilms = new ArrayList<>();
        expectedPopularFilms.add(guardian);
        expectedPopularFilms.add(testFilm);

        Assertions.assertEquals(expectedPopularFilms, popularFilms);
    }
}