package ru.yandex.practicum.filmorate.exception.film;

public class FilmDoNotExistException extends Throwable {

    public FilmDoNotExistException(String message) {
        super(message);
    }
}
