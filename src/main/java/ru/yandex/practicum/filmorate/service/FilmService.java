package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    @Autowired
    FilmStorage filmStorage;
    @Autowired
    UserStorage userStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film likeFilm(int id, int userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(id);
        film.getLikes().add(user);
        log.debug("Лайк поставлен!");
        return film;
    }

    public Film deleteLike(int id, int userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(id);
        boolean likeFound = film.getLikes().contains(user);
        if (!likeFound) {
            log.info("Пользователь {} не ставил лайк фильму {}", userId, id);
            throw new ValidationException("Лайк пользователя " + userId + " отсутствует");
        }
        film.getLikes().remove(user);
        log.debug("Лайк удален!");
        return film;
    }

    public List<Film> getMostLikedFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        return f1.getLikes().size() - f0.getLikes().size();
    }

}
