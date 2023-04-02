package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
@Slf4j
public class InMemoryUserDbStorage implements UserDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private long nextId = 1;

    public InMemoryUserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> getUserById(long id) {
        try {
            String sql = "SELECT * FROM PUBLIC.\"user\" WHERE \"user_id\" = ?";
            return Optional.of(jdbcTemplate.queryForObject(sql, this::makeUser, id));
        } catch (EmptyResultDataAccessException e) {
            log.info("Пользователь " + id + " не найден");
            throw new UserNotFoundException("Пользователь " + id + " не найден");
        }
    }

    public User addUser(User user) {
        if (!checkValidity(user)) {
            throw new ValidationException("Некорректные данные пользователя для добавления");
        }
        user.setId(nextId++);
        String sql = "INSERT INTO \"user\" (\"user_id\", \"login\", \"name\", \"email\", \"birthday\") \n" +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday().toString());
        return user;
    }

    public User updateUser(User user) {
        if (!checkValidity(user)) {
            throw new ValidationException("Некорректные данные пользователя для обновления");
        }
        Optional<User> foundUser = getUserById(user.getId());
        if (foundUser.isPresent()) {
            String sql = "UPDATE \"user\" SET \"login\" = ?, \"name\" = ?, \"email\" = ?, \"birthday\" = ? " +
                    "WHERE \"user_id\" = ?";
            jdbcTemplate.update(sql,
                    user.getLogin(),
                    user.getName(),
                    user.getEmail(),
                    user.getBirthday().toString(),
                    user.getId());
            log.debug("Данные пользователя {} обновлены!", foundUser.get().getId());
        } else {
            throw new UserNotFoundException("Пользователь " + user.getId() + " не найден ");
        }
        return user;
    }

    public List<User> getUsers() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    public User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getDate("birthday").toLocalDate()
        );
    }

    public boolean checkValidity(User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.info("Адрес электронной почты не содержит символ \"@\", либо пустой");
            return false;
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("Логин пустой, либо содержит пробел");
            return false;
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Поскольку имя пользователя не указано, им будет логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Дата рождения не может быть в будущем");
            return false;
        }
        log.debug("Валидация пройдена!");
        return true;
    }
}
