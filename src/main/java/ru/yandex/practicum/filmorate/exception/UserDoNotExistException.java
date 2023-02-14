package ru.yandex.practicum.filmorate.exception;

public class UserDoNotExistException extends RuntimeException {

    public UserDoNotExistException(String message) {
        super(message);
    }
}
