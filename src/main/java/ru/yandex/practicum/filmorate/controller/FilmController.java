package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@Slf4j
@Component
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        log.debug("Запрос на добавление фильма");
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        log.debug("Запрос на обновление данных фильма");
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.debug("Запрос на коллекцию фильмов");
        return filmService.getFilms();
    }

    @GetMapping("/films/{id}")
    public Optional<Film> getFilmById(@PathVariable long id) {
        log.debug("Запрос на получение данных конкретного фильма {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film likeFilm(@PathVariable long id,
                         @PathVariable long userId) throws Throwable {
        log.debug("Запрос на лайк пользователем {} фильма {}", userId, id);
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id,
                           @PathVariable long userId) {
        log.debug("Запрос на удаление лайка пользователя {} фильма {}", userId, id);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostLikedFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.debug("Запрос на список самых популярных фильмов");
        return filmService.getMostLikedFilms(count);
    }
}
