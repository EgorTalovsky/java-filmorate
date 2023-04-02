package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testAddFilm() {
        Film film = new Film(1, "film1", "film for test add", LocalDate.of(1996, 1, 21), 100, new Mpa(5, null));
        filmDbStorage.addFilm(film);
        assertThat(filmDbStorage.getFilmById(1).get()).hasFieldOrPropertyWithValue("name", "film1");
    }

    @Test
    public void testGetFilmWithIncorrectId() {
        assertThrows(FilmNotFoundException.class, () -> filmDbStorage.getFilmById(3));
    }

    @Test
    public void testGetFilmWithcorrectId() {
        assertThat(filmDbStorage.getFilmById(1).get()).hasFieldOrPropertyWithValue("description", "film for test add");
    }

    @Test
    public void testUpdateFilmWithCorrectFields() {
        Film filmForUpdate = new Film(1, "film1", "film for update", LocalDate.of(1996, 1, 21), 100, new Mpa(5, null));
        filmDbStorage.updateFilm(filmForUpdate);
        assertThat(filmDbStorage.getFilmById(1).get()).hasFieldOrPropertyWithValue("description","film for update");
    }

    @Test
    public void testUpdateFilmWithIncorrectFields() {
        Film filmWithoutName = new Film(1, "", "film for update", LocalDate.of(1996, 1, 21), 100, new Mpa(5, null));
        assertThrows(FilmNotFoundException.class, () -> filmDbStorage.updateFilm(filmWithoutName));
    }

    @Test
    public void testGetFilms() {
        assertThat(filmDbStorage.getFilms().size()).isEqualTo(0);
    }
}
