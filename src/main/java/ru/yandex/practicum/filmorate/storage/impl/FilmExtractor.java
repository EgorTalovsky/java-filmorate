package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmExtractor implements ResultSetExtractor<List<Film>> {
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final UserDbStorage userDbStorage;

    public FilmExtractor(MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage, UserDbStorage userDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> films = new LinkedHashMap<>();
        Film film;
        while (rs.next()) {
            Long id = rs.getLong("film_id");
            film = films.get(id);
            if (!films.containsKey(id)) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
                int duration = rs.getInt("duration");
                film = new Film(id, name, description, releaseDate, duration);
                film.setId(id);
                film.setMpa(mpaDbStorage.makeMpa(rs, 0).get());
                films.put(id, film);
            }
            if (rs.getInt("genre_id") != 0) {
                film.getGenres().add(genreDbStorage.makeGenre(rs, 0));
            }
            film.setGenres(film.getGenres().stream()
                    .sorted(genreDbStorage::compare)
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
            if (rs.getLong("user_id") != 0) {
                film.getLikes().add(userDbStorage.getUserById(rs.getLong("user_id")).get());
            }
        }
        return new ArrayList<>(films.values());
    }
}
