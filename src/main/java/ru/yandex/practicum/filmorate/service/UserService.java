package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserById(long id);

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);

    List<User> getFriendsOfUserId(long id);

    Set<User> getCommonFriends(long id, long otherId);
}
