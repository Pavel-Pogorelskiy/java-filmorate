package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa create(Mpa data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mpa uptade(Mpa data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mpa get(int id) {
        List<Mpa> mpa = jdbcTemplate.query("Select * From mpa Where mpa_id = ?",
                (rs, rowNum) -> Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("name"))
                        .build(),id);
        if (mpa.size() != 1) {
            throw new NotFoundDataException("В List больше или меньше 1 рейтинга");
        }
        return mpa.get(0);
    }

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.query("Select * From mpa order by mpa_id asc",
                (rs, rowNum) -> Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("name"))
                        .build());
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }
}
