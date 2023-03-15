package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class InMemoryUserService implements UserService {
    UserStorage userStorage;

    @Autowired
    public InMemoryUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(long id, long friendId) {
        if (id == friendId) {
            log.info("Некорректный запрос - нельзя добавить в друзья самого себя");
            throw new ValidationException("Некорректный запрос - нельзя добавить в друзья самого себя");
        }
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(id);
        log.debug("Пользователи {} и {} теперь друзья!", id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        if (id == friendId) {
            log.info("Некорректный запрос - нельзя удалить из друзей самого себя");
            throw new ValidationException("Некорректный запрос - нельзя удалить из друзей самого себя");
        }
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        boolean friendIsFound = user.getFriends().contains(friendId);
        if (!friendIsFound) {
            log.info("Пользователя {} нет в списке друзей у {}", friendId, id);
            throw new UserNotFoundException("Пользователя " + friendId + " нет в списке друзей у " + id);
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        log.debug("Пользователи {} и {} больше не друзья!", id, friendId);
    }

    public List<User> getFriendsOfUserId(long id) {
        List<User> friends = new ArrayList<>();
        for (Long friendId : userStorage.getUserById(id).getFriends()) {
            friends.add(userStorage.getUserById(friendId));
        }
        return friends;
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
}
