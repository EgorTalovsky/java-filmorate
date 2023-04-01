package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GenreDbStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM \"genre\"";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    public Optional<Genre> getGenreById(int id) {
        try {
            String sql = "SELECT * FROM \"genre\" WHERE \"genre_id\" = ?";
            return Optional.of(jdbcTemplate.queryForObject(sql, this::makeGenre, id));
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException("Жанр " + id + " не найден");
        }
    }

    public Set<Genre> getGenreByFilm(Film film) {
        try {
            Set<Genre> genreWithName = new HashSet<>();
            for (Genre genre : film.getGenres()) {
                genreWithName.add(getGenreById(genre.getId()).orElse(new Genre(0, "жанр не найден")));
            }
            return genreWithName
                    .stream().sorted(this::compare)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException("Жанр фильма " + film.getId() + "не найден");
        }
    }

    private int compare(Genre g0, Genre g1) {
        return g0.getId() - g1.getId();
    }

    public Set<Genre> getGenresByFilmId(long id) {
        try {
            String sql = "SELECT g.\"genre_id\", g.\"genre_name\"\n" +
                    "FROM \"film_genre\" fg JOIN \"genre\" g  ON fg.\"genre_id\" = g.\"genre_id\" \n" +
                    "WHERE \"film_id\" = ?";
            return new HashSet<>(jdbcTemplate.query(sql, this::makeGenre, id));
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException("Жанр фильма " + id + "не найден");
        }
    }

    public void addGenreInDbByFilm(Film film) {
        String sql = "INSERT INTO \"film_genre\" (\"film_id\", \"genre_id\") VALUES (?, ?);";
        for (Genre genre : getGenreByFilm(film)) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    public void deleteGenreInDbByFilmForUpdate(Film film) {
        String sql = "DELETE FROM \"film_genre\" WHERE \"film_id\" = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    public Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        );
    }

}
