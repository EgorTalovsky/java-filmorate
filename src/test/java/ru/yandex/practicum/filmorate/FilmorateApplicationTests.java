package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.*;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    MpaDbStorage mpaDbStorage = new MpaDbStorage(jdbcTemplate);
    GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
    UserDbStorage userStorage = new InMemoryUserDbStorage(jdbcTemplate);
    LikeDbStorage likeDbStorage = new LikeDbStorage(userStorage, jdbcTemplate);
    FilmDbStorage filmDbStorage = new InMemoryFilmDbStorage(jdbcTemplate, mpaDbStorage, genreDbStorage, likeDbStorage);

    @Test
    public void checkValidityWithCorrectFilm() {
        Film film = new Film(1, "name", "description", LocalDate.of(1996, 1, 21), 100, new Mpa(5, null));
        assertTrue(filmDbStorage.checkValidity(film));
    }

    @Test
    public void checkValidityWithFilmWithoutName() {
        Film film = new Film(1, "", "description", LocalDate.of(1996, 1, 21), 100, new Mpa(5, null));
        assertFalse(filmDbStorage.checkValidity(film));
    }

    @Test
    public void checkValidityWithSizeOfDescriptionIs200() {
        Film film = new Film(1, "name",
                "А я сейчас вам покажу, откуда на Беларусь готовилось нападение. " +
                        "И если бы за шесть часов до операции не был нанесён превентивный удар по позициям — " +
                        "четыре позиции, я сейчас покажу карту, привёз — они ",
                LocalDate.of(1996, 1, 21),
                100, new Mpa(5, null));
        assertTrue(filmDbStorage.checkValidity(film));
    }

    @Test
    public void checkValidityWithSizeOfDescriptionIs201() {
        Film film = new Film(1, "name",
                "А я сейчас вам покажу, откуда на Беларусь готовилось нападение." +
                        " И если бы за шесть часов до операции не был нанесён превентивный удар по позициям" +
                        " — четыре позиции, я сейчас покажу карту, привёз — они б",
                LocalDate.of(1996, 1, 21),
                100, new Mpa(5, null));
        assertFalse(filmDbStorage.checkValidity(film));
    }

    @Test
    public void checkValidityWithReleaseDateIs18951228() {
        Film film = new Film(1, "name",
                "description",
                LocalDate.of(1895, 12, 28),
                100, new Mpa(5, null));
        assertTrue(filmDbStorage.checkValidity(film));
    }

    @Test
    public void checkValidityWithReleaseDateIs18951227() {
        Film film = new Film(1, "name",
                "description",
                LocalDate.of(1895, 12, 27),
                100, new Mpa(5, null));
        assertFalse(filmDbStorage.checkValidity(film));
    }

    @Test
    public void checkValidityWithDurationOfFilmIs0() {
        Film film = new Film(1, "name", "description", LocalDate.of(1996, 1, 21), 0, new Mpa(5, null));
        assertFalse(filmDbStorage.checkValidity(film));
    }

    @Test
    public void checkValidityWithDurationOfFilmIs1() {
        Film film = new Film(1, "name", "description", LocalDate.of(1996, 1, 21), 1, new Mpa(5, null));
        assertTrue(filmDbStorage.checkValidity(film));
    }

    @Test
    public void checkValidityWithCorrectUser() {
        User user = new User("login", "name", "email@ya.ru",  LocalDate.of(1996, 1, 21));
        assertTrue(userStorage.checkValidity(user));
    }

    @Test
    public void checkValidityWithBlankEmail() {
        User user = new User("", "login", "name", LocalDate.of(1996, 1, 21));
        assertFalse(userStorage.checkValidity(user));
    }

    @Test
    public void checkValidityWithEmailWithoutAt() {
        User user = new User("emailya.ru", "login", "name", LocalDate.of(1996, 1, 21));
        assertFalse(userStorage.checkValidity(user));
    }

    @Test
    public void checkValidityWithEmptyLogin() {
        User user = new User("email@ya.ru", "", "name", LocalDate.of(1996, 1, 21));
        assertFalse(userStorage.checkValidity(user));
    }

    @Test
    public void checkValidityWithLoginWithSpace() {
        User user = new User("email@ya.ru", "log in", "name", LocalDate.of(1996, 1, 21));
        assertFalse(userStorage.checkValidity(user));
    }

    @Test
    public void checkValidityWithEmptyName() {
        User user = new User("", "login",  "email@ya.ru", LocalDate.of(1996, 1, 21));
        assertTrue(userStorage.checkValidity(user));
    }

    @Test
    public void checkEqualsLoginAndNameWithEmptyName() {
        User user = new User("", "login", "email@ya.ru",  LocalDate.of(1996, 1, 21));
        userStorage.checkValidity(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    public void checkValidityBirthdayNow() {
        User user = new User("login", "name", "email@ya.ru", LocalDate.now());
        assertTrue(userStorage.checkValidity(user));
    }

    @Test
    public void checkValidityBirthdayInFuture() {
        User user = new User("email@ya.ru", "login", "name", LocalDate.of(2026, 1, 21));
        assertFalse(userStorage.checkValidity(user));
    }
}