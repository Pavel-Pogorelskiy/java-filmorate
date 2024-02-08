package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.MarksStorage;


@Repository
@RequiredArgsConstructor
public class MarksDbStorage implements MarksStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addMarkFilm(int filmId, int userId, int mark) {

        jdbcTemplate.update("INSERT INTO marks values (?, ?, ?);", filmId, userId, mark);
    }

    @Override
    public void updateMarkFilm(int filmId, int userId, int mark) {

        jdbcTemplate.update("UPDATE marks SET mark = ?" +
                "WHERE film_id = ? AND user_id = ?;", mark, filmId, userId);
    }

    @Override
    public void removeMarkFilm(int filmId, int userId) {

        jdbcTemplate.update("DELETE FROM marks WHERE film_id = ? AND user_id = ?;", filmId, userId);
    }
}
