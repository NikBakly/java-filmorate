package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {
    private FilmController filmController;
    private UserService userService;
    private Film guardian;

    @BeforeEach
    void init(@Autowired FilmController filmController, @Autowired UserService userService) {
        this.filmController = filmController;
        this.userService = userService;
        guardian = Film.builder()
                .id(1L)
                .name("guardian")
                .description("Бывший агент элитных спецслужб спасает девочку")
                .duration(91)
                .mpa(Mpa.builder().id(1).name("G").build())
                .releaseDate(LocalDate.of(2012, 4, 26))
                .build();
    }

    @Test
    void test1_shouldThrowExceptionWithEmptyName() {
        Film film = Film.builder()
                .id(1L)
                .name("")
                .description("very cute")
                .releaseDate(LocalDate.of(2001, 12, 23))
                .duration(93)
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));

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
                .duration(134)
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> filmController.create(titanic));

        Assertions.assertEquals("Описание фильма не может превышать 200 символов", thrown.getMessage());
    }

    @Test
    void test3_shouldThrowExceptionIfReleaseDateBefore28December1985() {
        Film film = Film.builder()
                .id(1L)
                .name("test")
                .description("Test")
                .releaseDate(LocalDate.of(1890, 2, 20))
                .duration(50)
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));

        Assertions.assertEquals("Дата релиза не должна быть раньше чем 28 декабря 1985", thrown.getMessage());
    }

    @Test
    void test4_shouldThrowExceptionIfDurationIsNegative() {
        Film film = Film.builder()
                .id(1L)
                .name("test")
                .description("Test")
                .releaseDate(LocalDate.of(1900, 2, 20))
                .duration(-10)
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));

        Assertions.assertEquals("Продолжительность фильма должна быть положительной", thrown.getMessage());
    }

    @Test
    void test5_shouldThrowExceptionIfDurationIsZero() {
        Film film = Film.builder()
                .id(1L)
                .name("test")
                .description("Test")
                .releaseDate(LocalDate.of(1900, 2, 20))
                .duration(0)
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));

        Assertions.assertEquals("Продолжительность фильма должна быть положительной", thrown.getMessage());
    }

    @Test
    void test6_shouldSuccessfullySaveFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("film")
                .description("film")
                .releaseDate(LocalDate.of(1999, 2, 20))
                .mpa(Mpa.builder().id(1).name("G").build())
                .duration(60)
                .build();

        filmController.create(film);
        Assertions.assertTrue(filmController.findAll().contains(film));
    }

    @Test
    void test7_shouldAddLike() {
        filmController.create(guardian);
        User firstUser = User.builder()
                .id(1L)
                .email("test1@ya.ru")
                .login("test1")
                .name("testing1")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        userService.create(firstUser);
        filmController.addLike(guardian.getId(), 1L);

        Long expectedRate = filmController.findFilmById(guardian.getId()).getRate();
        Assertions.assertEquals(1L, expectedRate);
    }

    @Test
    void test8_shouldDeleteLike() {
        filmController.create(guardian);
        User firstUser = User.builder()
                .email("test1@ya.ru")
                .login("test1")
                .name("testing1")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        User secondUser = User.builder()
                .email("test2@ya.ru")
                .login("test2")
                .name("testing2")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        userService.create(firstUser);
        userService.create(secondUser);

        filmController.addLike(guardian.getId(), 1L);
        filmController.addLike(guardian.getId(), 2L);

        filmController.deleteLike(guardian.getId(), 1L);

        Long expectedRate = filmController.findFilmById(guardian.getId()).getRate();
        Assertions.assertEquals(1L, expectedRate);
    }

    @Test
    void test9_shouldGetPopularFilms() {
        Film testFilm = Film.builder()
                .id(2L)
                .name("test")
                .description("very cute test")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .mpa(Mpa.builder().id(1).name("G").build())
                .duration(25)
                .build();

        filmController.create(guardian);
        filmController.create(testFilm);

        User firstUser = User.builder()
                .email("test1@ya.ru")
                .login("test1")
                .name("testing1")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        User secondUser = User.builder()
                .email("test2@ya.ru")
                .login("test2")
                .name("testing2")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        User thirdUser = User.builder()
                .email("test3@ya.ru")
                .login("test3")
                .name("testing3")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        userService.create(firstUser);
        userService.create(secondUser);
        userService.create(thirdUser);

        filmController.addLike(guardian.getId(), 1L);
        filmController.addLike(guardian.getId(), 2L);
        guardian = guardian.toBuilder().rate(2L).build();

        filmController.addLike(testFilm.getId(), 3L);
        testFilm = testFilm.toBuilder().rate(1L).build();

        List<Film> popularFilms = filmController.getPopularFilms(2);

        List<Film> expectedPopularFilms = new ArrayList<>();
        expectedPopularFilms.add(guardian);
        expectedPopularFilms.add(testFilm);

        Assertions.assertEquals(expectedPopularFilms, popularFilms);
    }

    @Test
    void test10_shouldDeleteFilmById() {
        filmController.create(guardian);

        filmController.delete(guardian.getId());

        Collection<Film> expected = filmController.findAll();

        Assertions.assertEquals(0, filmController.findAll().size());
    }
}