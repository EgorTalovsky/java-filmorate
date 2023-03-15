package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final static LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    public Film addFilm(Film film) throws ValidationException {
        if (!checkValidity(film)) {
            log.info("Валидация не пройдена");
            throw new ValidationException("Фильм не может быть добавлен в фильмотеку");
        }
        film.setId(films.size() + 1);
        films.put(film.getId(), film);
        log.debug("Фильм добавлен!");
        return film;
    }

    public Film updateFilm(Film film) {
        if (!checkValidity(film)) {
            log.info("Валидация не пройдена");
            throw new ValidationException("Фильм не может быть обновлен");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Данные фильма обновлены!");
        } else {
            log.info("Фильм не найден");
            throw new FilmNotFoundException("Фильм отсутствует в фильмотеке.");
        }
        return film;
    }

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public Film getFilmById(long id) {
        if (films.containsKey(id)) {
            log.debug("Фильм {} найден", id);
            return films.get(id);
        } else {
            log.debug("Фильм {} не найден", id);
            throw new FilmNotFoundException("Фильм " + id + " отсутствует в фильмотеке.");
        }
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
