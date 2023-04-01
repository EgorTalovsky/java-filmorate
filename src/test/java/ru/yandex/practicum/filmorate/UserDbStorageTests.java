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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @Test
    public void testAddUser() {
        User user = new User(1, "user1", "for test Add", "user1@ya.ru", LocalDate.of(1996, 1, 21));
        userStorage.addUser(user);
        assertThat(userStorage.getUserById(1).get()).hasFieldOrPropertyWithValue("login", "user1");
    }

    @Test
    public void testGetUserByCorrectId() {
        User userForTestGet = new User(1, "user1", "for test Get", "user1@ya.ru", LocalDate.of(1996, 1, 21));
        userStorage.addUser(userForTestGet);
        Optional<User> userOptional = userStorage.getUserById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testGetUserByIncorrectId() {
        assertThrows(UserNotFoundException.class, () -> userStorage.getUserById(4));
    }

    @Test
    public void testUpdateUserWithCorrectFields() {
        User userForCorrectUpdate = new User(1, "user1", "Update", "user1@ya.ru", LocalDate.of(1996, 1, 21));
        userStorage.updateUser(userForCorrectUpdate);
        assertThat(userStorage.getUserById(1).get())
                .hasFieldOrPropertyWithValue("name", "Update");
    }

    @Test
    public void testUpdateUserWithIncorrectFields() {
        User userForIncorrectUpdate = new User(1, "user1", "IncorrectUpdate", "user1@ya.ru", LocalDate.of(2025, 1, 21));
        assertThrows(ValidationException.class, () -> userStorage.updateUser(userForIncorrectUpdate));
    }

    @Test
    public void testGetUsers() {
        System.out.println(userStorage.getUsers());
        assertThat(userStorage.getUsers().size()).isEqualTo(1);
    }

}