package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.MpaDbStorage;

import java.util.List;
import java.util.Optional;

@RestController
@Component
public class MpaController {
    @Autowired
    private final MpaDbStorage mpaDbStorage;

    public MpaController(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    @GetMapping("/mpa")
    public List<Optional<Mpa>> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Optional<Mpa> getMpaById(@PathVariable int id) {
        return mpaDbStorage.getMpaById(id);
    }
}
