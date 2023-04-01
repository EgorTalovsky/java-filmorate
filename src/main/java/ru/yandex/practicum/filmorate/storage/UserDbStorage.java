package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDbStorage {

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    Optional<User> getUserById(long id);

    boolean checkValidity(User user);

    User makeUser(ResultSet rs, int rowNum) throws SQLException;

}
