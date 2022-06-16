package ru.yandex.practicum.storage.filmLikes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmLikesDbStorage implements FilmLikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmLikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        String sqlQuery1 = "UPDATE FILMS SET RATE = RATE + 1 ";
        jdbcTemplate.update(sqlQuery1);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        String sqlQuery1 = "UPDATE FILMS SET RATE = RATE - 1 ";
        jdbcTemplate.update(sqlQuery1);
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, "
                + "f.MPA_ID, COUNT(u.USER_ID) AS likes_count FROM FILMS f "
                + "LEFT OUTER JOIN FILM_LIKES u ON f.FILM_ID = u.FILM_ID "
                + "GROUP BY f.FILM_ID ORDER BY likes_count DESC LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int mpaId = resultSet.getInt("MPA_ID");
        String sqlFindName = "SELECT MPA_ID, NAME FROM MPA WHERE MPA_ID = ?";
        Mpa mpa = jdbcTemplate.queryForObject(sqlFindName, this::mapRowToMpa, mpaId);

        return Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .rate(resultSet.getLong("likes_count"))
                .mpa(mpa)
                .build();
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
