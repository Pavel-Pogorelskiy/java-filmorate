package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void get() {
        Mpa mpaTest = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        MpaDbStorage mpaStorage = new MpaDbStorage(jdbcTemplate);
        Mpa mpa = mpaStorage.get(1);
        assertThat(mpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpaTest);
    }

    @Test
    void getAll() {
        Mpa mpaTest1 = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        Mpa mpaTest2 = Mpa.builder()
                .id(2)
                .name("PG")
                .build();
        Mpa mpaTest3 = Mpa.builder()
                .id(3)
                .name("PG-13")
                .build();
        Mpa mpaTest4 = Mpa.builder()
                .id(4)
                .name("R")
                .build();
        Mpa mpaTest5 = Mpa.builder()
                .id(5)
                .name("NC-17")
                .build();
        MpaDbStorage mpaStorage = new MpaDbStorage(jdbcTemplate);
        List<Mpa> mpa = mpaStorage.getAll();
        assertThat(mpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(mpaTest1, mpaTest2, mpaTest3, mpaTest4, mpaTest5));
    }
}