package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmDbStorage implements FilmDbStorage {
    private final static LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private long nextId = 1;
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final LikeDbStorage likeDbStorage;

    public InMemoryFilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage,
                                 GenreDbStorage genreDbStorage, LikeDbStorage likeDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
    }

    public Film addFilm(Film film) throws ValidationException {
        if (!checkValidity(film)) {
            log.info("Валидация не пройдена");
            throw new ValidationException("Фильм не может быть добавлен в фильмотеку");
        }
        film.setId(nextId++);
        film.setMpa(mpaDbStorage.getMpaById(film.getMpa().getId()).get());
        film.setGenres(genreDbStorage.getGenreByFilm(film));
        String sql = "INSERT INTO \"film\" (\"film_id\", \"name\", \"description\" , \"release_date\", \"duration\", \"age_rate_id\")\n" +
                "VALUES (?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().toString(),
                film.getDuration(),
                film.getMpa().getId());
        genreDbStorage.addGenreInDbByFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (!checkValidity(film)) {
            log.info("Валидация не пройдена");
            throw new ValidationException("Фильм не может быть обновлен");
        }
        Optional<Film> foundFilm = getFilmById(film.getId());
        if (foundFilm.isPresent()) {
            film.setMpa(mpaDbStorage.getMpaById(film.getMpa().getId()).get());
            film.setGenres(genreDbStorage.getGenreByFilm(film));
            String sql = "UPDATE \"film\" SET \"name\" = ? , \"description\" = ?, \"release_date\" = ?, " +
                    "\"duration\" = ?, \"age_rate_id\" = ? WHERE \"film_id\" = ?";
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate().toString(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            log.debug("Данные фильма {} обновлены!", film.getId());
            genreDbStorage.deleteGenreInDbByFilmForUpdate(film);
            genreDbStorage.addGenreInDbByFilm(film);
        } else {
            log.info("Фильм {} не найден", film.getId());
            throw new FilmNotFoundException("Фильм отсутствует в фильмотеке.");
        }
        return film;
    }

    public Optional<Film> getFilmById(long id) {
        try {
            String sql = "SELECT *\n" +
                    "FROM \"film\"\n" +
                    "WHERE \"film_id\"  = ?";
            Film film = jdbcTemplate.queryForObject(sql, this::makeFilm, id);
            film.setGenres(genreDbStorage.getGenresByFilmId(id));
            film.setLikes(likeDbStorage.getUserLikesByFilmId(id));
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            log.info("Фильм " + id + " не найден");
            throw new FilmNotFoundException("Фильм " + id + " не найден");
        }
    }

    public List<Film> getFilms() {
        String sql = "SELECT * FROM \"film\"";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm);
        for (Film film : films) {
            film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
            film.setLikes(likeDbStorage.getUserLikesByFilmId(film.getId()));
        }
        return films;
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                mpaDbStorage.getMpaById(rs.getInt("age_rate_id")).get());
    }


    public boolean checkValidity(Film film) {
        if (film.getName().isBlank()) {
            log.info("Название фильма не может быть пустым");
            return false;
        }
        if (film.getDescription().length() > 200) {
            log.info("Описание фильма превышает 200 символов");
            return false;
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.info("Дата выхода фильма не может быть раньше чем 28 декабря 1895 года");
            return false;
        }
        if (film.getDuration() <= 0) {
            log.info("Продолжительность фильма не может быть отрицательной");
            return false;
        }
        log.debug("Валидация пройдена!");
        return true;
    }


}
