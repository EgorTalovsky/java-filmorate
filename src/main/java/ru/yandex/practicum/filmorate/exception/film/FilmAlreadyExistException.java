package ru.yandex.practicum.filmorate.exception.film;

public class FilmAlreadyExistException extends Throwable {

    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
