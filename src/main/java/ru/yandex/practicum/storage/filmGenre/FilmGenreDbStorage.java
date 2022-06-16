package ru.yandex.practicum.storage.filmGenre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeSet;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //создания связи фильма и жанра в таблице
    @Override
    public void create(Long filmId, Long genreId) {
        String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    //получения фильма по его id
    @Override
    public TreeSet<Integer> getByFilmId(Long filmId) {
        String sqlQuery = "SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?";
        List<Integer> genreList = jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
        return new TreeSet<>(genreList);
    }

    //удаления всех строк в таблице с переданным id фильма
    @Override
    public void deleteByFilmId(Long filmId) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
