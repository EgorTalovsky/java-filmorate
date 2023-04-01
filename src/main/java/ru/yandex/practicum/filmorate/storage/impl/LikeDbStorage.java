package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.HashSet;
import java.util.Set;

@Component
public class LikeDbStorage {
    private UserDbStorage userDbStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public LikeDbStorage(UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<User> getUserLikesByFilmId(long filmId) {
        String sql = "SELECT u.\"user_id\", u.\"email\", u.\"name\", u.\"login\", u.\"birthday\"\n" +
                "FROM \"user\" u JOIN \"film_like\" fl ON u.\"user_id\" = fl.\"user_id\" \n" +
                "WHERE fl.\"film_id\" = ?";
        return new HashSet<>(jdbcTemplate.query(sql, userDbStorage::makeUser, filmId));
    }
}
