package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.film.FilmDoNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        if (!checkValidity(film)) {
            throw new ValidationException("Фильм не может быть добавлен в фильмотеку");
        }
        film.setId(films.size() + 1);
        films.put(film.getId(), film);
        log.debug("Фильм {} успешно добавлен! Id = {}", film.getName(), film.getId());
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) throws FilmDoNotExistException, ValidationException {
        if (!checkValidity(film)) {
            throw new ValidationException("Фильм не может быть обновлен");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Фильм {} успешно обновлен! Id = {}", film.getName(), film.getId());
        } else {
            throw new FilmDoNotExistException("Фильм " + film.getName() + " отсутствует в фильмотеке.");
        }
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.debug("Размер коллекции фильмов " +  films.size());
        return new ArrayList<>(films.values());
    }

    public boolean checkValidity(@RequestBody Film film) {
        LocalDate firstFilmDate = LocalDate.of(1895,12,28);
        if (film.getName().isBlank()) {
            log.info("Название фильма не может быть пустым");
            return false;
            //throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.info("Описание фильма более 200 символов. Количество - " + film.getDescription().length());
            return false;
            //throw new ValidationException("Описание фильма более 200 символов. Количество - " + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(firstFilmDate)) {
            log.info("Дата выхода фильма не может быть раньше 28.12.1895");
            return false;
            //throw new ValidationException("Дата выхода фильма не может быть раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.info("Продолжительность фильма должна быть положительной");
            return false;
            //throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        return true;
    }
}
