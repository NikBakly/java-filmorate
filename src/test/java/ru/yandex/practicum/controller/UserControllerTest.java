package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;

@SpringBootTest
class UserControllerTest {
    private final UserController controller = new UserController();

    @Test
    void test1_shouldThrowExceptionIfEmailWithoutCat() {
        User user = User.builder()
                .id(1)
                .email("nikita")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

        Assertions.assertEquals("Поле с email не может быть пустым или email должен содержать '@'",
                thrown.getMessage());
    }

    @Test
    void test2_shouldThrowExceptionIfEmailIsEmpty() {
        User user = User.builder()
                .id(1)
                .email("")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

        Assertions.assertEquals("Поле с email не может быть пустым или email должен содержать '@'",
                thrown.getMessage());
    }

    @Test
    void test3_shouldThrowExceptionIfLoginIsEmpty() {
        User user = User.builder()
                .id(1)
                .email("nik@ya.ru")
                .login("")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

        Assertions.assertEquals("Логин не должен быть пустым и при этом не должен содержать пробелы",
                thrown.getMessage());
    }

    @Test
    void test4_shouldThrowExceptionIfLoginContainsSpaces() {
        User user = User.builder()
                .id(1)
                .email("nik@ya.ru")
                .login("lo gin")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

        Assertions.assertEquals("Логин не должен быть пустым и при этом не должен содержать пробелы",
                thrown.getMessage());
    }

    @Test
    void test5_shouldThrowExceptionIfDateOfBirthInFuture() {
        User user = User.builder()
                .id(1)
                .email("nik@ya.ru")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2030, 12, 10))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

        Assertions.assertEquals("День рождения не может быть в будущем",
                thrown.getMessage());
    }

    @Test
    void test6_shouldLoginAndNicknameBeSameIfNicknameIsEmpty() {
        User user = User.builder()
                .id(1)
                .email("nik@ya.ru")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2021, 12, 10))
                .build();

        controller.create(user);

        //по логике программы должно произойти то же самое
        user = user.toBuilder().name("login").build();

        Assertions.assertTrue(controller.findAll().contains(user));
    }
}