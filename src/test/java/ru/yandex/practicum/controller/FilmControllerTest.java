package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;

import java.time.Duration;
import java.time.LocalDate;

@SpringBootTest
class FilmControllerTest {
    private final FilmController controller = new FilmController();

    @Test
    void test1_shouldThrowExceptionWithEmptyName() {
        Film film = Film.builder()
                .id(1)
                .name("")
                .description("very cute")
                .releaseDate(LocalDate.of(2001, 12, 23))
                .duration(Duration.ofMinutes(93))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(film));

        Assertions.assertEquals("Название фильма не может быть пустым", thrown.getMessage());
    }

    @Test
    void test2_shouldThrowExceptionIfDescriptionMore200Characters() {
        Film titanic = Film.builder()
                .id(1)
                .name("Titanic")
                .description("В первом и последнем плавании шикарного «Титаника» встречаются двое." +
                        " Пассажир нижней палубы Джек выиграл билет в карты," +
                        " а богатая наследница Роза отправляется в Америку, чтобы выйти замуж по расчёту." +
                        " Чувства молодых людей только успевают расцвести, и даже не классовые различия создадут" +
                        " испытания влюблённым, а айсберг, вставший на пути считавшегося непотопляемым лайнера.")
                .releaseDate(LocalDate.of(1998, 2, 20))
                .duration(Duration.ofMinutes(134))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(titanic));

        Assertions.assertEquals("Описание фильма не может превышать 200 символов", thrown.getMessage());
    }

    @Test
    void test3_shouldThrowExceptionIfReleaseDateBefore28December1985() {
        Film film = Film.builder()
                .id(1)
                .name("test")
                .description("Test")
                .releaseDate(LocalDate.of(1890, 2, 20))
                .duration(Duration.ofMinutes(50))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(film));

        Assertions.assertEquals("Дата релиза не должна быть раньше чем 28 декабря 1985", thrown.getMessage());
    }

    @Test
    void test4_shouldThrowExceptionIfDurationIsNegative() {
        Film film = Film.builder()
                .id(1)
                .name("test")
                .description("Test")
                .releaseDate(LocalDate.of(1900, 2, 20))
                .duration(Duration.ofMinutes(-10))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(film));

        Assertions.assertEquals("Продолжительность фильма должна быть положительной", thrown.getMessage());
    }

    @Test
    void test5_shouldThrowExceptionIfDurationIsZero() {
        Film film = Film.builder()
                .id(1)
                .name("test")
                .description("Test")
                .releaseDate(LocalDate.of(1900, 2, 20))
                .duration(Duration.ofMinutes(0))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(film));

        Assertions.assertEquals("Продолжительность фильма должна быть положительной", thrown.getMessage());
    }

    @Test
    void test6_shouldSuccessfullySaveFilm() {
        Film film = Film.builder()
                .id(1)
                .name("film")
                .description("film")
                .releaseDate(LocalDate.of(1999, 2, 20))
                .duration(Duration.ofMinutes(60))
                .build();

        controller.create(film);

        Assertions.assertTrue(controller.findAll().contains(film));
    }
}