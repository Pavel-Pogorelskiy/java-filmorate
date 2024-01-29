package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre create(Genre data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Genre uptade(Genre data) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Genre get(int id) {
         List<Genre> genre = jdbcTemplate.query(
                 "Select * From genre Where genre_id = ?",
                (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build(), id);
         if (genre.size() != 1) {
             throw new NotFoundDataException("В List больше или меньше 1 жанра");
         }
        return genre.get(0);
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query(
                "Select * From genre",
                (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build());
    }

    @Override
    public void delete(int id) {
    }
}
