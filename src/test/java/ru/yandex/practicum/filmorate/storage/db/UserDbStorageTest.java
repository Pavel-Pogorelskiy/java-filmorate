package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

     @AfterEach
     public void newDateBase() {
         jdbcTemplate.update("drop table if exists mpa, films, likes,users,friends,genre,genre_link;\n" +
                 "\n" +
                 "CREATE TABLE IF NOT EXISTS mpa (\n" +
                 "  mpa_id integer PRIMARY KEY,\n" +
                 "  name varchar(255) NOT null unique\n" +
                 ");\n" +
                 "CREATE TABLE IF NOT EXISTS films (\n" +
                 "  film_id integer generated by default as identity not null PRIMARY key,\n" +
                 "  name varchar(255) NOT NULL,\n" +
                 "  description varchar(255),\n" +
                 "  releaseDate date NOT null,\n" +
                 "  duration integer NOT NULL,\n" +
                 "  mpa integer NOT null REFERENCES mpa (mpa_id) on delete cascade\n" +
                 ");\n" +
                 "\n" +
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
                 ")");
         jdbcTemplate.update("insert into mpa (mpa_id, name) values (1, 'G'),(2, 'PG'),(3, 'PG-13'),(4, 'R'),(5, 'NC-17')");
         jdbcTemplate.update("insert into genre (genre_id, name) values (1, 'Комедия'),(2, 'Драма'),(3, 'Мультфильм'),(4, 'Триллер'),(5, 'Документальный'),(6, 'Боевик')");
    }

    @Test
    void create() {
        User newUser = User.builder()
                .id(1)
                .name("Чушпан")
                .login("chushpan")
                .email("cheshpan@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(newUser);
        User savedUser = userStorage.get(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    void uptade() {
        User newUser = User.builder()
                .id(1)
                .name("Чушпан")
                .login("chushpan")
                .email("cheshpan@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User uptadeUser = User.builder()
                .id(1)
                .name("Пацан")
                .login("pacan")
                .email("pacan@mail.ru")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(newUser);
        userStorage.uptade(uptadeUser);
        User savedUser = userStorage.get(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(uptadeUser);

    }

    @Test
    void get() {
            User newUser = User.builder()
                    .id(1)
                    .name("Чушпан")
                    .login("chushpan")
                    .email("cheshpan@mail.ru")
                    .birthday(LocalDate.of(1990, 1, 1))
                    .build();
            UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
            userStorage.create(newUser);
            User savedUser = userStorage.get(1);
            assertThat(savedUser)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(newUser);
    }

    @Test
    void getAll() {
        User newUser = User.builder()
                .id(1)
                .name("Чушпан")
                .login("chushpan")
                .email("cheshpan@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User newUser2 = User.builder()
                .id(2)
                .name("Чушпан2")
                .login("chushpan2")
                .email("cheshpan2@mail.ru")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(newUser);
        userStorage.create(newUser2);
        List<User> savedUser = userStorage.getAll();
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(newUser,newUser2));
    }

    @Test
    void delete() {
        User newUser = User.builder()
                .id(1)
                .name("Чушпан")
                .login("chushpan")
                .email("cheshpan@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User newUser2 = User.builder()
                .id(2)
                .name("Чушпан2")
                .login("chushpan2")
                .email("cheshpan2@mail.ru")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(newUser);
        userStorage.create(newUser2);
        List<User> savedUsers = userStorage.getAll();
        assertThat(savedUsers)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(newUser,newUser2));
        userStorage.delete(1);
        User savedUser = userStorage.get(2);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser2);
    }
}