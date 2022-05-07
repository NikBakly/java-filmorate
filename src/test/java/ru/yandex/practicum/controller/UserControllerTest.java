package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.IdUserService;
import ru.yandex.practicum.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class UserControllerTest {
    private UserController controller;
    private User nikita;
    private User sasha;

    UserControllerTest() {
    }

    @BeforeEach
    void init(){
        UserStorage userStorage = new InMemoryUserStorage( new IdUserService());
        controller = new UserController(userStorage);
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

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

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

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

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

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

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

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

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

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> controller.create(user));

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

        controller.create(user);

        //по логике программы должно произойти то же самое
        user = user.toBuilder().name("login").build();

        Assertions.assertTrue(controller.findAll().contains(user));
    }

    @Test
    void test7_shouldAddToFriend() {
        controller.create(nikita);
        controller.create(sasha);

        controller.addToFriend(nikita.getId(), sasha.getId());

        Assertions.assertTrue(nikita.getFriends().contains(2L) && sasha.getFriends().contains(1L));
    }

    @Test
    void test8_shouldDeleteFriend() {
        controller.create(nikita);
        controller.create(sasha);
        controller.addToFriend(nikita.getId(), sasha.getId());

        controller.deleteFriend(nikita.getId(), sasha.getId());

        Assertions.assertFalse(nikita.getFriends().contains(2L) && sasha.getFriends().contains(1L));
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

        controller.create(nikita);
        controller.create(sasha);
        controller.create(vanya);
        controller.create(alex);
        controller.create(maks);

        controller.addToFriend(nikita.getId(), sasha.getId());
        controller.addToFriend(nikita.getId(), vanya.getId());
        controller.addToFriend(nikita.getId(), maks.getId());

        controller.addToFriend(sasha.getId(), vanya.getId());
        controller.addToFriend(sasha.getId(), alex.getId());

        List<User> mutualFriends = controller.getMutualFriends(nikita.getId(), sasha.getId());
        Assertions.assertFalse(mutualFriends.size() == 1 && mutualFriends.contains(vanya));
    }


}