package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeFilm(int filmId, int userId) {
        try {
            jdbcTemplate.update("Insert into likes values (?,?)", filmId, userId);
        } catch (DataAccessException ignored) {
        }
    }

        @Override
    public void removeLikeFilm(int filmId, int userId) {
        jdbcTemplate.update(
                "Delete From likes Where user_id = ? " +
                        "And film_id = ? ", userId, filmId);
    }
}
