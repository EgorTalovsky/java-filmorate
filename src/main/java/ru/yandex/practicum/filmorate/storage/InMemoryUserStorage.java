package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public User addUser(User user) {
        if (!checkValidity(user)) {
            log.info("Некорректные данные пользователя");
            throw new ValidationException("Некорректные данные пользователя");
        }
        user.setUserId(users.size() + 1);
        users.put(user.getUserId(), user);
        log.debug("Пользователь добавлен!");
        return user;
    }

    public User updateUser(User user) {
        if (!checkValidity(user)) {
            log.info("Некорректные данные пользователя для обновления");
            throw new ValidationException("Некорректные данные пользователя для обновления");
        }
        if (users.containsKey(user.getUserId())) {
            users.put(user.getUserId(), user);
            log.debug("Данные пользователя обновлены!");
        } else {
            log.info("Пользователь не найден");
            throw new UserNotFoundException("Пользователь не найден");
        }
        return user;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(long id) {
        if (users.containsKey(id)) {
            log.debug("Пользователь {} найден", id);
            return users.get(id);
        } else {
            log.info("Пользователь {} не найден", id);
            throw new UserNotFoundException("Пользователь " + id + " не найден");
        }

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
