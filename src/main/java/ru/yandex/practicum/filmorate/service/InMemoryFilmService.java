package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InMemoryFilmService implements FilmService {
    FilmDbStorage filmDbStorage;
    UserDbStorage userStorage;
    JdbcTemplate jdbcTemplate;

    @Autowired
    public InMemoryFilmService(FilmDbStorage filmDbStorage, UserDbStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.filmDbStorage = filmDbStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film addFilm(Film film) {
        return filmDbStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmDbStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmDbStorage.getFilms();
    }

    public Optional<Film> getFilmById(long id) {
        return filmDbStorage.getFilmById(id);
    }

    public Film likeFilm(long filmId, long userId) throws Throwable {
        User user = userStorage.getUserById(userId).get();
        Film film = filmDbStorage.getFilmById(filmId).orElseThrow((Supplier<Throwable>) () -> new FilmNotFoundException("Film nit found"));
        film.getLikes().add(user);
        String sql = "INSERT INTO \"film_like\" (\"film_id\", \"user_id\") VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.debug("Лайк фильму {} от пользователя {} поставлен!", filmId, userId);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        User user = userStorage.getUserById(userId).get();
        Film film = filmDbStorage.getFilmById(filmId).get();
        boolean likeFound = film.getLikes().contains(user);
        if (!likeFound) {
            log.info("Пользователь {} не ставил лайк фильму {}", userId, filmId);
            throw new ValidationException("Лайк пользователя " + userId + " отсутствует");
        }
        String sql = "DELETE \"film_like\" WHERE \"film_id\" = ? AND \"user_id\" = ?";
        jdbcTemplate.update(sql, filmId, userId);
        film.getLikes().remove(user);
        log.debug("Лайк удален!");
        return film;
    }

    public List<Film> getMostLikedFilms(Integer count) {
        return filmDbStorage.getFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        return f1.getLikes().size() - f0.getLikes().size();
    }
}
