package ru.yandex.practicum.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.friendship.FriendshipStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendshipStorage friendshipStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendshipStorage = friendshipStorage;
    }

    @Override
    public User create(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("У пользователя поле nickname и login одинаковый, т.к. поле nickname было пустое");
            user = user.toBuilder().name(user.getLogin()).build();
        }
        String sqlQuery = "INSERT INTO users(LOGIN, NAME, EMAIL, BIRTHDAY) " +
                "VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        User finalUser = user;
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, finalUser.getLogin());
            stmt.setString(2, finalUser.getName());
            stmt.setString(3, finalUser.getEmail());
            stmt.setDate(4, Date.valueOf(finalUser.getBirthday()));
            return stmt;
        }, keyHolder);
        long key = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findUserById(key);
    }

    @Override
    public User update(User user) {
        checkUser(user.getId());
        String sqlQuery = "UPDATE USERS SET LOGIN = ?, NAME= ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return findUserById(user.getId());
    }

    @Override
    public void delete(Long userId) {
        checkUser(userId);
        String sqlQuery = "DELETE FROM users WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    public User findUserById(Long userId) {
        checkUser(userId);
        String sqlQuery = "SELECT USER_ID, LOGIN, NAME, EMAIL, BIRTHDAY " +
                "FROM USERS WHERE USER_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (row.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } else
            return null;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT USER_ID, LOGIN, NAME, EMAIL, BIRTHDAY FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        Set<Long> idList = new HashSet<>();
        for (User user : friendshipStorage.getFriends(resultSet.getLong("USER_ID"))) {
            idList.add(user.getId());
        }
        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .friends(idList)
                .build();
    }

    private void validate(User user) {
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("У пользователя поле login пустое или содержит пробел");
            throw new ValidationException("Логин не должен быть пустым и при этом не должен содержать пробелы");
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("У пользователя поле email пустое или не содержит '@'");
            throw new ValidationException("Поле с email не может быть пустым или email должен содержать '@'");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("У пользователя поле dateOfBirth не правильно определено");
            throw new ValidationException("День рождения не может быть в будущем");
        }
    }

    private void checkUser(Long userId) {
        String sqlQuery = "SELECT USER_ID " +
                "FROM USERS WHERE USER_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (!row.next()) {
            log.warn("Пользователь под id " + userId + " не найден");
            throw new NotFoundException("Пользователь под id " + userId + " не найден");
        }
    }
}
