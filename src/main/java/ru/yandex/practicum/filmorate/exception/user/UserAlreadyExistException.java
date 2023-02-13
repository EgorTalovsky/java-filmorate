package ru.yandex.practicum.filmorate.exception.user;

public class UserAlreadyExistException extends Throwable {

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
