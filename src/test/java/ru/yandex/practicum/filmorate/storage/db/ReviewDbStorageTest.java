package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testCreateAndFindReviewById() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Film newFilm = Film.builder()
                .id(1)
                .name("New Film")
                .description("Description")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(10)
                .mpa(mpa)
                .build();

        User newUser = User.builder()
                .id(1)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Review newReview = Review.builder()
                .id(1)
                .content("Content")
                .isPositive(true)
                .filmId(1)
                .userId(1)
                .useful(0)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(newFilm);

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(newUser);

        ReviewDbStorage reviewDbStorage = new ReviewDbStorage(jdbcTemplate);
        reviewDbStorage.create(newReview);

        Review savedReview = reviewDbStorage.get(1);

        assertThat(savedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newReview);
    }
}
