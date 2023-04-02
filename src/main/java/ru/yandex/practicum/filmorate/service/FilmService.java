package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Optional<Film> getFilmById(long id);

    Film likeFilm(long id, long userId) throws Throwable;

    Film deleteLike(long id, long userId);

    List<Film> getMostLikedFilms(Integer count);
}
