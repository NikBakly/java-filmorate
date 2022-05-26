package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class UserControllerTest {
    private UserController controllerUser;
    private User nikita;
    private User sasha;

    UserControllerTest() {
    }

    @BeforeEach
    void init() {
        UserStorage userStorage = new InMemoryUserStorage();
        controllerUser = new UserController(userStorage);
        nikita = User.builder()
                .id(1L)
                .email("nikita@ya.ru")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        sasha = User.builder()
                .id(2L)
                .email("sasha@ya.ru")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
    }

    @Test
    void test1_shouldThrowExceptionIfEmailWithoutCat() {
        User user = User.builder()
                .id(1L)
                .email("nikita")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerUser.create(user));

        Assertions.assertEquals("Поле с email не может быть пустым или email должен содержать '@'",
                thrown.getMessage());
    }

    @Test
    void test2_shouldThrowExceptionIfEmailIsEmpty() {
        User user = User.builder()
                .id(1L)
                .email("")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerUser.create(user));

        Assertions.assertEquals("Поле с email не может быть пустым или email должен содержать '@'",
                thrown.getMessage());
    }

    @Test
    void test3_shouldThrowExceptionIfLoginIsEmpty() {
        User user = User.builder()
                .id(1L)
                .email("nik@ya.ru")
                .login("")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerUser.create(user));

        Assertions.assertEquals("Логин не должен быть пустым и при этом не должен содержать пробелы",
                thrown.getMessage());
    }

    @Test
    void test4_shouldThrowExceptionIfLoginContainsSpaces() {
        User user = User.builder()
                .id(1L)
                .email("nik@ya.ru")
                .login("lo gin")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerUser.create(user));

        Assertions.assertEquals("Логин не должен быть пустым и при этом не должен содержать пробелы",
                thrown.getMessage());
    }

    @Test
    void test5_shouldThrowExceptionIfDateOfBirthInFuture() {
        User user = User.builder()
                .id(1L)
                .email("nik@ya.ru")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2030, 12, 10))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controllerUser.create(user));

        Assertions.assertEquals("День рождения не может быть в будущем",
                thrown.getMessage());
    }

    @Test
    void test6_shouldLoginAndNicknameBeSameIfNicknameIsEmpty() {
        User user = User.builder()
                .id(1L)
                .email("nik@ya.ru")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2021, 12, 10))
                .build();

        controllerUser.create(user);

        //по логике программы должно произойти то же самое
        user = user.toBuilder().name("login").build();

        Assertions.assertTrue(controllerUser.findAll().contains(user));
    }

    @Test
    void test7_shouldAddToFriend() {
        controllerUser.create(nikita);
        controllerUser.create(sasha);

        controllerUser.addToFriend(nikita.getId(), sasha.getId());

        Assertions.assertTrue(nikita.getFriends().containsKey(2L) && sasha.getFriends().containsKey(1L));
    }

    @Test
    void test8_shouldDeleteFriend() {
        controllerUser.create(nikita);
        controllerUser.create(sasha);
        controllerUser.addToFriend(nikita.getId(), sasha.getId());

        controllerUser.deleteFriend(nikita.getId(), sasha.getId());

        Assertions.assertFalse(nikita.getFriends().containsKey(2L) && sasha.getFriends().containsKey(1L));
    }

    @Test
    void test9_shouldGetMutualFriends() {
        User vanya = User.builder()
                .id(3L)
                .email("vanya@ya.ru")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        User alex = User.builder()
                .id(4L)
                .email("vanya@ya.ru")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        User maks = User.builder()
                .id(4L)
                .email("maks@ya.ru")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();

        controllerUser.create(nikita);
        controllerUser.create(sasha);
        controllerUser.create(vanya);
        controllerUser.create(alex);
        controllerUser.create(maks);

        controllerUser.addToFriend(nikita.getId(), sasha.getId());
        controllerUser.addToFriend(nikita.getId(), vanya.getId());
        controllerUser.addToFriend(nikita.getId(), maks.getId());

        controllerUser.addToFriend(sasha.getId(), vanya.getId());
        controllerUser.addToFriend(sasha.getId(), alex.getId());

        List<User> mutualFriends = controllerUser.getMutualFriends(nikita.getId(), sasha.getId());
        Assertions.assertFalse(mutualFriends.size() == 1 && mutualFriends.contains(vanya));
    }

    @Test
    void test10_shouldDeleteFilmById() {
        controllerUser.create(nikita);

        controllerUser.delete(nikita.getId());

        Assertions.assertEquals(0, controllerUser.findAll().size());
    }
}