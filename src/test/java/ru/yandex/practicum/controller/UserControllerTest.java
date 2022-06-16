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
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    private UserController userController;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    void init(@Autowired UserController userController) {
        this.userController = userController;
        firstUser = User.builder()
                .id(1L)
                .email("test1@ya.ru")
                .login("test1")
                .name("testing1")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        secondUser = User.builder()
                .id(2L)
                .email("test2@ya.ru")
                .login("test2")
                .name("testing2")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
    }

    @Test
    void test1_shouldThrowExceptionIfEmailWithoutCat() {
        User user = User.builder()
                .email("nikita")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));

        Assertions.assertEquals("Поле с email не может быть пустым или email должен содержать '@'",
                thrown.getMessage());
    }

    @Test
    void test2_shouldThrowExceptionIfEmailIsEmpty() {
        User user = User.builder()
                .email("")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));

        Assertions.assertEquals("Поле с email не может быть пустым или email должен содержать '@'",
                thrown.getMessage());
    }

    @Test
    void test3_shouldThrowExceptionIfLoginIsEmpty() {
        User user = User.builder()
                .email("nik@ya.ru")
                .login("")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));

        Assertions.assertEquals("Логин не должен быть пустым и при этом не должен содержать пробелы",
                thrown.getMessage());
    }

    @Test
    void test4_shouldThrowExceptionIfLoginContainsSpaces() {
        User user = User.builder()
                .email("nik@ya.ru")
                .login("lo gin")
                .name("nickname")
                .birthday(LocalDate.of(2002, 7, 16))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));

        Assertions.assertEquals("Логин не должен быть пустым и при этом не должен содержать пробелы",
                thrown.getMessage());
    }

    @Test
    void test5_shouldThrowExceptionIfDateOfBirthInFuture() {
        User user = User.builder()
                .email("nik@ya.ru")
                .login("login")
                .name("nickname")
                .birthday(LocalDate.of(2030, 12, 10))
                .build();

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));

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

        userController.create(user);

        //по логике программы должно произойти то же самое
        user = user.toBuilder().name("login").build();

        Assertions.assertTrue(userController.findAll().contains(user));
    }

    @Test
    void test7_shouldAddToFriend() {
        userController.create(firstUser);
        userController.create(secondUser);

        userController.addToFriend(firstUser.getId(), secondUser.getId());

        Assertions.assertTrue(userController.findUserById(firstUser.getId()).getFriends().contains(2L) &&
                !userController.findUserById(secondUser.getId()).getFriends().contains(1L));
    }

    @Test
    void test8_shouldDeleteFriend() {
        userController.create(firstUser);
        userController.create(secondUser);
        userController.addToFriend(firstUser.getId(), secondUser.getId());

        userController.deleteFriend(firstUser.getId(), secondUser.getId());

        Assertions.assertFalse(firstUser.getFriends().contains(2L) && secondUser.getFriends().contains(1L));
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

        userController.create(firstUser);
        userController.create(secondUser);
        userController.create(vanya);

        userController.addToFriend(firstUser.getId(), secondUser.getId());
        userController.addToFriend(firstUser.getId(), vanya.getId());
        userController.addToFriend(secondUser.getId(), vanya.getId());

        List<User> mutualFriends = userController.getMutualFriends(firstUser.getId(), secondUser.getId());
        Assertions.assertTrue(mutualFriends.size() == 1 && mutualFriends.contains(vanya));
    }

    @Test
    void test10_shouldDeleteFilmById() {
        userController.create(firstUser);

        userController.delete(firstUser.getId());

        Assertions.assertEquals(0, userController.findAll().size());
    }
}