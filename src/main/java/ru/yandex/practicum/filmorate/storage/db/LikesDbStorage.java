package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Primary
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeFilm(int filmId, int userId) {
        jdbcTemplate.update("Insert into likes values (?,?)", filmId, userId);
    }

    @Override
    public void removeLikeFilm(int filmId, int userId) {
        jdbcTemplate.update(
                "Delete From likes Where user_id = ? " +
                        "And film_id = ? ", userId, filmId);
    }

    @Override
    public List<Film> getFilms(int limit) {
        List<Film> films = jdbcTemplate.query(
                "select f.film_id, r.rate, f.name, " +
                        "f.description, f.releaseDate, f.duration, " +
                        "m.mpa_id, m.name as mpa_name from " +
                        "(select l.film_id as film_id, " +
                        "count(l.user_id) as rate from likes l " +
                        "group by l.film_id) as r " +
                        "right join films as f on r.film_id = f.film_id " +
                        "join mpa m on f.mpa = m.mpa_id  " +
                        "order by r.rate desc limit ?",
                FilmDbStorage::createFilm, limit);
        return FilmDbStorage.fillGenres(films, jdbcTemplate);
    }

    public List<Film> getFilmsFilteredByYear(int limit, int year) {

        String sql =
                "SELECT f.film_id, r.rate, f.name, " +
                        "f.description, f.releaseDate, f.duration, " +
                        "m.mpa_id, m.name AS mpa_name " +
                "FROM (select l.film_id as film_id, " +
                        "count(l.user_id) AS rate FROM likes AS l " +
                "GROUP BY l.film_id) AS r " +
                "RIGHT JOIN films as f ON r.film_id = f.film_id " +
                "JOIN mpa m ON f.mpa = m.mpa_id " +
                "WHERE EXTRACT(YEAR FROM f.releaseDate) = ? "  +
                "ORDER BY r.rate DESC " +
                "LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sql, FilmDbStorage::createFilm, year, limit);

        return FilmDbStorage.fillGenres(films, jdbcTemplate);
    }

    public List<Film> getFilmsFilteredByGenre(int limit, int genreId) {

        String sql =
                "SELECT mp.* FROM " +

                    "(SELECT f.film_id, r.rate, f.name, " +
                        "f.description, f.releaseDate, f.duration, " +
                        "m.mpa_id, m.name AS mpa_name " +
                    "FROM (select l.film_id as film_id, " +
                        "count(l.user_id) AS rate FROM likes AS l " +
                    "GROUP BY l.film_id) AS r " +
                    "RIGHT JOIN films as f ON r.film_id = f.film_id " +
                    "JOIN mpa m ON f.mpa = m.mpa_id) AS mp " +

                "WHERE mp.film_id IN " +
                    "(SELECT film_id FROM genre_link " +
                    "WHERE genre_id = ?) " +
                "ORDER BY mp.rate DESC " +
                "LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sql, FilmDbStorage::createFilm, genreId, limit);

        return FilmDbStorage.fillGenres(films, jdbcTemplate);
    }

    public List<Film> getFilmsFilteredByGenreAndYear(int limit, int genreId, int year) {

        String sql =
                "SELECT mp.* FROM " +

                    "(SELECT f.film_id, r.rate, f.name, " +
                        "f.description, f.releaseDate, f.duration, " +
                        "m.mpa_id, m.name AS mpa_name " +
                    "FROM (select l.film_id as film_id, " +
                        "count(l.user_id) AS rate FROM likes AS l " +
                    "GROUP BY l.film_id) AS r " +
                    "RIGHT JOIN films as f ON r.film_id = f.film_id " +
                    "JOIN mpa m ON f.mpa = m.mpa_id " +
                    "WHERE EXTRACT(YEAR FROM f.releaseDate) = ?) AS mp "  +

                "WHERE mp.film_id IN " +
                    "(SELECT film_id FROM genre_link " +
                    "WHERE genre_id = ?) " +
                "ORDER BY mp.rate DESC " +
                "LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sql, FilmDbStorage::createFilm, year, genreId, limit);

        return FilmDbStorage.fillGenres(films, jdbcTemplate);
    }


}
