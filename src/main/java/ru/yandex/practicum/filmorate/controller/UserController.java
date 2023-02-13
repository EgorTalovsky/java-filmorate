package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.user.UserDoNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();

    @PostMapping("/users")
    public User addUser(@RequestBody User user) throws UserAlreadyExistException, ValidationException {
        if (!checkValidity(user)) {
            throw new ValidationException("Некорректные данные пользователя");
        }
        user.setId(users.size() + 1);
        users.put(user.getId(), user);
        log.debug("Пользователь {} успешно добавлен! Id = {}", user.getName(), user.getId());
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) throws UserDoNotExistException, ValidationException {
        if (!checkValidity(user)) {
            throw new ValidationException("Некорректные данные пользователя для обновления");
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Данные пользователя " + user.getLogin() + " успешно обновлены");
        } else {
            throw new UserDoNotExistException("Пользователь с почтой " + user.getEmail() + " не найден.");
        }
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.trace("Нас уже " + users.size());
        return new ArrayList<>(users.values());
    }

    public boolean checkValidity(@RequestBody User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.info("Проверьте корректность введенной почты");
            return false;
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("Проверьте корректность логина");
            return false;
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не было введено. В качестве имени будет использован логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Дата рождения не может быть в будущем");
            return false;
        }
        return true;
    }


}
