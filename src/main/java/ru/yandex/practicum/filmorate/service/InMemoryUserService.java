package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
public class InMemoryUserService implements UserService {
    UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public InMemoryUserService(UserDbStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public Optional<User> getUserById(long id) {
        return userStorage.getUserById(id);
    }


    public void addFriend(long id, long friendId) {
        if (id == friendId) {
            log.info("Некорректный запрос - нельзя добавить в друзья самого себя");
            throw new ValidationException("Некорректный запрос - нельзя добавить в друзья самого себя");
        }
        Optional<User> user = userStorage.getUserById(id);
        Optional<User> friend = userStorage.getUserById(friendId);
        if (user.isPresent() && friend.isPresent()
                && !getFriendsOfUserId(user.get().getId()).contains(friend.get())) {
            String sql = "INSERT INTO \"friends_users\"  (\"user_id\", \"friend_id\") \n" +
                    "VALUES (?,?)";
            jdbcTemplate.update(sql, id, friendId);
            log.debug("Пользователь {} теперь в списке друзей {} !", friendId, id);
        } else {
            throw new UserNotFoundException("Пользователь не найден, либо они уже друзья");
        }
    }

    public void deleteFriend(long id, long friendId) {
        if (id == friendId) {
            log.info("Некорректный запрос - нельзя удалить из друзей самого себя");
            throw new ValidationException("Некорректный запрос - нельзя удалить из друзей самого себя");
        }
        Optional<User> user = userStorage.getUserById(id);
        Optional<User> friend = userStorage.getUserById(friendId);
        if (user.isPresent() && friend.isPresent()
                && getFriendsOfUserId(user.get().getId()).contains(friend.get())) {
            String sql = "DELETE \"friends_users\" WHERE \"user_id\" = ? AND \"friend_id\" = ?";
            jdbcTemplate.update(sql, id, friendId);
            log.debug("Пользователь {} удалил {} из друзей!", id, friendId);
        } else {
            throw new UserNotFoundException("Пользователь не найден, либо они не друзья");
        }
    }

    public List<User> getFriendsOfUserId(long id) {
        String sql = "SELECT * " +
                     "FROM \"user\" " +
                     "WHERE \"user_id\" IN " +
                        "(SELECT \"friend_id\" " +
                        "FROM \"friends_users\" " +
                        "WHERE \"user_id\" = ?);";
        return jdbcTemplate.query(sql,this::makeFriends, id);
    }

    public Set<User> getCommonFriends (long id, long otherId) {
        if (id == otherId) {
            log.info("Некорректный запрос - нельзя найти общих друзей у самого себя");
            throw new ValidationException("Некорректный запрос - нельзя найти общих друзей у самого себя");
        }
        Set<User> commonFriends = new HashSet<>();
        for (User friend : getFriendsOfUserId(id)) {
            if (getFriendsOfUserId(otherId).contains(friend)) {
                commonFriends.add(friend);
            }
        }
        if (commonFriends.size() > 0) {
            log.debug("У пользователей {} и {} найдено {} общих друзей", id, otherId, commonFriends.size());
        } else {
            log.debug("У пользователей {} и {} отсутствуют общие друзья", id, otherId);
        }
        return commonFriends;
    }

    private User makeFriends(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
