package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;

import java.util.List;
import java.util.Optional;

@RestController
@Component
public class GenreController {
    @Autowired
    private final GenreDbStorage genreDbStorage;

    public GenreController(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Optional<Genre> getGenreById(@PathVariable int id) {
        return genreDbStorage.getGenreById(id);
    }
}
