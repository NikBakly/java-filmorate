package ru.yandex.practicum.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.MPA;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        validate(film);
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, MPA_ID, RELEASE_DATE, DURATION, RATE) " +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setLong(3, film.getMpa().getId());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getDuration());
            stmt.setLong(6, film.getRate());
            return stmt;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findFilmById(id);
    }

    @Override
    public Film update(Film film) {
        checkFilm(film.getId());
        String sqlQuery = "UPDATE FILMS SET "
                + "NAME = ?, DESCRIPTION = ?,  MPA_ID = ?,  RELEASE_DATE = ?, DURATION = ?, RATE = ? "
                + "WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getId());
        return findFilmById(film.getId());
    }

    @Override
    public void deleteById(Long filmId) {
        checkFilm(filmId);
        String sqlQuery = "DELETE FROM FILMS where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Film findFilmById(Long filmId) {
        checkFilm(filmId);
        String sqlQuery = "SELECT FILM_ID, NAME, DESCRIPTION, MPA_ID, RELEASE_DATE, DURATION, RATE " +
                "FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (row.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } else {
            return null;
        }
    }

    @Override
    public Collection<Film> findALl() {
        String sqlQuery = "SELECT FILM_ID, NAME, DESCRIPTION, MPA_ID, RELEASE_DATE, DURATION, RATE " +
                "FROM FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int MPAId = resultSet.getInt("MPA_ID");
        String sqlFindName = "SELECT MPA_ID, NAME FROM MPA WHERE MPA_ID = ?";
        MPA mpa = jdbcTemplate.queryForObject(sqlFindName, this::mapRowToMpa, MPAId);

        return Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .mpa(mpa)
                .rate(resultSet.getLong("RATE"))
                .duration(resultSet.getInt("DURATION"))
                .build();
    }

    private MPA mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return MPA.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }

    private void checkFilm(Long filmId) {
        String sqlQuery = "SELECT FILM_ID " +
                "FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (!row.next()) {
            log.warn("Фильм id " + filmId + " не найден");
            throw new NotFoundException("Фильм id " + filmId + " не найден");
        }
    }

    private void validate(@Valid Film film) {
        if (film.getName().isBlank()) {
            log.warn("У фильма поле name пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200 || film.getDescription().isBlank()) {
            log.warn("У фильма поле description содержит более 200 символов");
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 11, 28))) {
            log.warn("У фильма поле realeseDate раньше даты 28 декабря 1985");
            throw new ValidationException("Дата релиза не должна быть раньше чем 28 декабря 1985");
        }
        if (film.getDuration() <= 0) {
            log.warn("У фильма поле duration не является положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getMpa() == null) {
            log.warn("У фильма поле MPA не определено");
            throw new ValidationException("У фильма поле MPA не определено");
        }
    }
}
