package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class InMemoryFilmDbStorage implements FilmDbStorage {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final FilmExtractor filmExtractor;
    private long nextId = 1;

    @Autowired
    public InMemoryFilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage,
                                 FilmExtractor filmExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.filmExtractor = filmExtractor;
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
        try {
            if (!checkValidity(film)) {
                log.info("Валидация не пройдена");
                throw new ValidationException("Фильм не может быть обновлен");
            }
            if (getFilmById(film.getId()).isPresent()) {
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
            }
            return film;
        } catch (RuntimeException e) {
            throw new FilmNotFoundException("Фильм отсутствует в фильмотеке.");
        }
    }

    public Optional<Film> getFilmById(long id) {
        try {
            String sql = "SELECT f.\"film_id\", f.\"name\", f.\"description\", f.\"release_date\", f.\"duration\" , f.\"age_rate_id\",\n" +
                    "ar.\"rate_name\", g.\"genre_id\", g.\"genre_name\", fl.\"user_id\"  \n" +
                    "FROM \"film\" f \n" +
                    "LEFT JOIN \"age_rate\" ar ON f.\"age_rate_id\" = ar.\"age_rate_id\" \n" +
                    "LEFT JOIN \"film_genre\" fg ON f.\"film_id\" = fg.\"film_id\" \n" +
                    "LEFT JOIN \"genre\" g ON fg.\"genre_id\" = g.\"genre_id\" \n" +
                    "LEFT JOIN \"film_like\" fl ON f.\"film_id\" = fl.\"film_id\"\n" +
                    "WHERE f.\"film_id\" = ?";
            Film film = jdbcTemplate.query(sql, filmExtractor, id).get(0);
            return Optional.of(film);
        } catch (RuntimeException e) {
            log.info("Фильм " + id + " не найден");
            throw new FilmNotFoundException("Фильм " + id + " не найден");
        }
    }

    public List<Film> getFilms() {
        String sql = "SELECT f.\"film_id\", f.\"name\", f.\"description\", f.\"release_date\", f.\"duration\" , f.\"age_rate_id\",\n" +
                "ar.\"rate_name\", g.\"genre_id\", g.\"genre_name\", fl.\"user_id\"  \n" +
                "FROM \"film\" f \n" +
                "LEFT JOIN \"age_rate\" ar ON f.\"age_rate_id\" = ar.\"age_rate_id\" \n" +
                "LEFT JOIN \"film_genre\" fg ON f.\"film_id\" = fg.\"film_id\" \n" +
                "LEFT JOIN \"genre\" g ON fg.\"genre_id\" = g.\"genre_id\" \n" +
                "LEFT JOIN \"film_like\" fl ON f.\"film_id\" = fl.\"film_id\"";
        List<Film> films = jdbcTemplate.query(sql, filmExtractor);
        return films;
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
