package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTests {
    private final UserDbStorage userStorage;
    User user = new User(1, "user1", "for test Add", "user1@ya.ru", LocalDate.of(1996, 1, 21));
    User userForCorrectUpdate = new User(1, "user1", "Update", "user1@ya.ru", LocalDate.of(1996, 1, 21));
    User userForIncorrectUpdate = new User(1, "user1", "IncorrectUpdate", "user1@ya.ru", LocalDate.of(2025, 1, 21));

    @Test
    public void testAddUser() {
        userStorage.addUser(user);
        assertThat(userStorage.getUserById(1).get()).hasFieldOrPropertyWithValue("login", "user1");
    }

    @Test
    public void testGetUserByCorrectId() {
        Optional<User> userOptional = userStorage.getUserById(2);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 2L));
    }

    @Test
    public void testGetUserByIncorrectId() {
        assertThrows(UserNotFoundException.class, () -> userStorage.getUserById(4));
    }

    @Test
    public void testUpdateUserWithCorrectFields() {
        userStorage.updateUser(userForCorrectUpdate);
        assertThat(userStorage.getUserById(1).get())
                .hasFieldOrPropertyWithValue("name", "Update");
    }

    @Test
    public void testUpdateUserWithIncorrectFields() {
        assertThrows(ValidationException.class, () -> userStorage.updateUser(userForIncorrectUpdate));
    }

    @Test
    public void testGetUsers() {
        System.out.println(userStorage.getUsers());
        assertThat(userStorage.getUsers().size()).isEqualTo(1);
    }

}