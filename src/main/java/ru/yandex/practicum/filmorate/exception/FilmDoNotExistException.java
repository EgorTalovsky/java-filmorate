package ru.yandex.practicum.filmorate.exception;

public class FilmDoNotExistException extends RuntimeException {

    public FilmDoNotExistException(String message) {
        super(message);
    }
}
