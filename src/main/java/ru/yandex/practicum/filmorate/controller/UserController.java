package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@Slf4j
@Component
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) throws ValidationException {
        log.debug("Запрос на добавление пользователя");
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) throws UserNotFoundException, ValidationException {
        log.debug("Запрос на обновление данных пользователя");
        return userService.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.debug("Запрос на список пользователей");
        return new ArrayList<>(userService.getUsers());
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable long id) {
        log.debug("Запрос на получение данных конкретного пользователя {}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id,
                          @PathVariable long friendId) {
        log.debug("Запрос на добавление в друзья пользователей {} и {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id,
                          @PathVariable long friendId) {
        log.debug("Запрос на удаление из друзей пользователей {} и {}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping ("/users/{id}/friends")
    public List<User> getFriendsOfUserId(@PathVariable long id) {
        log.debug("Запрос на список друзей пользователя {}", id);
        return userService.getFriendsOfUserId(id);
    }

    @GetMapping ("/users/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends (@PathVariable long id,
                                          @PathVariable long otherId) {
        log.debug("Запрос на список общих друзей пользователей {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
