package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    public void newDateBase() {
        jdbcTemplate.update("drop table if exists mpa, films, likes,users,friends,genre,genre_link,reviews,like_review;\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS mpa (\n" +
                "  mpa_id integer PRIMARY KEY,\n" +
                "  name varchar(255) NOT null unique\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS films (\n" +
                "  film_id integer generated by default as identity not null PRIMARY key,\n" +
                "  name varchar(255) NOT NULL,\n" +
                "  description varchar(200),\n" +
                "  releaseDate date NOT null,\n" +
                "  duration integer NOT NULL,\n" +
                "  mpa integer NOT null REFERENCES mpa (mpa_id) on delete cascade\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS users (\n" +
                "  user_id integer generated by default as identity not null PRIMARY key,\n" +
                "  email varchar UNIQUE NOT NULL,\n" +
                "  login varchar(255) NOT NULL,\n" +
                "  name varchar(255) NOT NULL,\n" +
                "  birthday date NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS likes (\n" +
                "film_id integer REFERENCES films (film_id) on delete cascade,\n" +
                "user_id integer REFERENCES users (user_id) on delete cascade,\n" +
                "  PRIMARY KEY (film_id, user_id)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS friends (\n" +
                "  user_id integer references users (user_id) on delete cascade,\n" +
                "  friends_id integer references users (user_id) on delete cascade,\n" +
                "  PRIMARY KEY (user_id, friends_id)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS genre (\n" +
                "  genre_id integer PRIMARY KEY,\n" +
                "  name varchar(255) NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS genre_link (\n" +
                "  film_id integer REFERENCES films (film_id) on delete cascade,\n" +
                "  genre_id integer REFERENCES genre (genre_id) on delete cascade,\n" +
                "  PRIMARY KEY (film_id, genre_id)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS reviews (\n" +
                "  review_id integer generated by default as identity not null PRIMARY key,\n" +
                "  content varchar(255) NOT NULL,\n" +
                "  isPositive boolean,\n" +
                "  user_id integer references users (user_id) on delete cascade,\n" +
                "  film_id integer REFERENCES films (film_id) on delete cascade,\n" +
                "  useful integer DEFAULT 0\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS like_review (\n" +
                "  review_id integer REFERENCES reviews (review_id) on delete cascade,\n" +
                "  user_id integer REFERENCES users (user_id) on delete cascade,\n" +
                "  isLike boolean,\n" +
                "  PRIMARY KEY (review_id, user_id)\n" +
                ");");
        jdbcTemplate.update("insert into mpa (mpa_id, name) values (1, 'G'),(2, 'PG')," +
                "(3, 'PG-13'),(4, 'R'),(5, 'NC-17')");
        jdbcTemplate.update("insert into genre (genre_id, name) values (1, 'Комедия'),(2, 'Драма')," +
                "(3, 'Мультфильм'),(4, 'Триллер'),(5, 'Документальный'),(6, 'Боевик')");
    }

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
