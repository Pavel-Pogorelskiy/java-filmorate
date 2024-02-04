package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import ru.yandex.practicum.filmorate.storage.DirectorStogare;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Primary
public class DirectorDbStorage implements DirectorStogare {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director get(int directorId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE director_id = ?", directorId);
        rs.next();
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public List<Director> findAll() {
        List<Director> directors = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM directors");
        while (rs.next()) {
            Director director = Director.builder()
                    .id(rs.getInt("director_id"))
                    .name(rs.getString("name"))
                    .build();
            directors.add(director);
        }
        return directors;
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(director.directorRowMap()).intValue());
        return get(director.getId());
    }

    @Override
    public Director put(Director director) {
        isRegistered(director.getId());
        jdbcTemplate.update(
                "UPDATE directors SET name = ? WHERE director_id = ?",
                director.getName(), director.getId());
        return director;
    }

    public boolean isRegistered(int directorId) {
        String sqlQuery = "SELECT * FROM directors WHERE director_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlQuery, directorId);
        return genreRow.next();
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(
                "DELETE FROM directors WHERE director_id = ?", id);
    }
}
