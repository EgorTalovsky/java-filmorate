package ru.yandex.practicum.filmorate.exception.user;

public class UserDoNotExistException extends Throwable {

    public UserDoNotExistException(String message) {
        super(message);
    }
}
