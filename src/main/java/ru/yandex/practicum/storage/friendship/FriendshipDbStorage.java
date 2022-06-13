package ru.yandex.practicum.storage.friendship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addToFriend(Long userId, Long friendId) {
        validateCheckUser(userId);
        validateCheckUser(friendId);
        if (!checkFriendStatus(userId, friendId)) {
            if (checkFriendStatus(friendId, userId)) {
                if (!checkConfirmedFriends(friendId, userId)) {
                    String sqlQuery = "UPDATE FRIENDSHIP SET STATUS = true WHERE USER_ID = ? AND FRIEND_ID = ?";
                    jdbcTemplate.update(sqlQuery, friendId, userId);
                }
            } else {
                String sqlQuery = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS)  VALUES (?, ?, false)";
                jdbcTemplate.update(sqlQuery, userId, friendId);
            }
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        validateCheckUser(userId);
        validateCheckUser(friendId);
        if (checkFriendStatus(friendId, userId)) {
            if (checkConfirmedFriends(friendId, userId)) {
                String sqlQuery = "UPDATE FRIENDSHIP SET STATUS = false WHERE USER_ID = ? AND FRIEND_ID = ?";
                jdbcTemplate.update(sqlQuery, friendId, userId);
            }
        } else {
            if (checkFriendStatus(userId, friendId)) {
                if (checkConfirmedFriends(userId, friendId)) {
                    String sqlQueryDelete = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
                    jdbcTemplate.update(sqlQueryDelete, userId, friendId);
                    String sqlQueryInsert = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS)  "
                            + "VALUES (?, ?, false)";
                    jdbcTemplate.update(sqlQueryInsert, friendId, userId);
                } else {
                    String sqlQuery = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
                    jdbcTemplate.update(sqlQuery, userId, friendId);
                }
            }
        }
    }

    @Override
    public List<User> getMutualFriends(Long userId, Long friendId) {
        validateCheckUser(userId);
        validateCheckUser(friendId);
        String sqlQuery = "SELECT * FROM ((SELECT USER_ID AS user_id FROM FRIENDSHIP "
                + "WHERE FRIEND_ID = ? "
                + "UNION ALL "
                + "SELECT FRIEND_ID AS user_id FROM FRIENDSHIP "
                + "WHERE USER_ID = ?) INTERSECT (SELECT USER_ID AS user_id "
                + "FROM FRIENDSHIP WHERE FRIEND_ID = ? UNION ALL "
                + "SELECT FRIEND_ID AS user_id FROM FRIENDSHIP "
                + "WHERE USER_ID = ?)) AS common_friends "
                + "INNER JOIN USERS u ON u.user_id = common_friends.user_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, userId, friendId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        validateCheckUser(userId);
        String sqlQuery = "SELECT * FROM (SELECT USER_ID AS user_id FROM FRIENDSHIP "
                + "WHERE FRIEND_ID = ? AND STATUS = true "
                + "UNION ALL "
                + "SELECT FRIEND_ID AS user_id FROM FRIENDSHIP "
                + "WHERE USER_ID = ?) AS users_id INNER JOIN USERS u ON u.USER_ID = users_id.user_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, userId);
    }

    // Маппинг полей пользователя из таблицы в объект
    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .name(resultSet.getString("NAME"))
                .login(resultSet.getString("LOGIN"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }

    // Являются ли пользователи неподтвержденными друзьями
    private boolean checkFriendStatus(Long userId, Long friendId) {
        String sqlQuery = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        int rowCount = 0;
        while (row.next()) {
            rowCount++;
        }
        return rowCount != 0;
    }

    // Являются ли пользователи подтвержденными друзьями
    private boolean checkConfirmedFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        row.next();
        return row.getBoolean("STATUS");
    }

    private void validateCheckUser(Long userId) {
        String sqlQuery = "SELECT USER_ID " +
                "FROM USERS WHERE USER_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (!row.next()) {
            log.warn("Пользователь под id " + userId + " не найден");
            throw new NotFoundException("Пользователь под id " + userId + " не найден");
        }
    }
}
