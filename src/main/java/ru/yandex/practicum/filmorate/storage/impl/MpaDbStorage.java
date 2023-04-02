package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Optional<Mpa>> getAllMpa() {
        String sql = "SELECT * FROM \"age_rate\"";
        return jdbcTemplate.query(sql, this::makeMpa);
    }

    public Optional<Mpa> getMpaById(int id) {
        try {
            String sql = "SELECT * FROM \"age_rate\" WHERE \"age_rate_id\" = ?";
            Optional<Mpa> mpa = jdbcTemplate.queryForObject(sql, this::makeMpa, id);
            return mpa;
        } catch (EmptyResultDataAccessException e) {
            throw new MpaNotFoundException("Рейтинг с id " + id + " не найден");
        }
    }

    public Optional<Mpa> makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return Optional.of(new Mpa(
                rs.getInt("age_rate_id"),
                rs.getString("rate_name")
        ));
    }
}
