package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.MpaDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage = new MpaDbStorage(new JdbcTemplate());
    Film film = new Film(1, "film1", "film for test add", LocalDate.of(1996, 1, 21), 100, new Mpa(5, null));
    Film filmForUpdate = new Film(1, "film1", "film for update", LocalDate.of(1996, 1, 21), 100, new Mpa(5, null));
    Film filmWithoutName = new Film(1, "", "film for update", LocalDate.of(1996, 1, 21), 100, new Mpa(5, null));

    @Test
    public void testAddFilm() {
        filmDbStorage.addFilm(film);
        assertThat(filmDbStorage.getFilmById(1).get()).hasFieldOrPropertyWithValue("name", "film1");
    }

    @Test
    public void testGetFilmWithIncorrectId() {
        assertThrows(FilmNotFoundException.class, () -> filmDbStorage.getFilmById(3));
    }

    @Test
    public void testGetFilmWithcorrectId() {
        assertThat(filmDbStorage.getFilmById(2).get()).hasFieldOrPropertyWithValue("description", "byDataSql");
    }

    @Test
    public void testUpdateFilmWithCorrectFields() {
        filmDbStorage.updateFilm(filmForUpdate);
        assertThat(filmDbStorage.getFilmById(1).get()).hasFieldOrPropertyWithValue("description","film for update");
    }

    @Test
    public void testUpdateFilmWithIncorrectFields() {
        assertThrows(ValidationException.class, () -> filmDbStorage.updateFilm(filmWithoutName));
    }

    @Test
    public void testGetFilms() {
        assertThat(filmDbStorage.getFilms().size()).isEqualTo(1);
    }
}
