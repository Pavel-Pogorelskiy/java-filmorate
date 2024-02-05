package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
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
    private final FilmDbStorage filmDbStorage;

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
        films = FilmDbStorage.fillGenres(films, jdbcTemplate);
        for (Film film : films) {
            film.setDirectors(filmDbStorage.getFilmDirectors(film.getId()));
        }

        return films;
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
        films = FilmDbStorage.fillGenres(films, jdbcTemplate);
        for (Film film : films) {
            film.setDirectors(filmDbStorage.getFilmDirectors(film.getId()));
        }

        return films;
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
        films = FilmDbStorage.fillGenres(films, jdbcTemplate);
        for (Film film : films) {
            film.setDirectors(filmDbStorage.getFilmDirectors(film.getId()));
        }

        return films;
    }

    public List<Film> recommendationsFilms(int userId) {
        String sql =
                "SELECT f.film_id, f.name, description, releaseDate, duration, " +
                        "mp.mpa_id, mp.name AS mpa_name \n" +
                        "FROM likes l\n" +
                        "LEFT JOIN films f ON l.film_id = f.film_id\n" +
                        "LEFT JOIN mpa mp ON f.mpa = mp.mpa_id\n" +
                        "WHERE l.user_id IN (SELECT l.user_id FROM likes l\n" +
                        "WHERE l.film_id IN (SELECT film_id FROM likes\n" +
                        "WHERE user_id = ?)\n" +
                        "AND l.user_id != ?\n" +
                        "GROUP BY l.user_id ORDER BY COUNT(l.film_id) DESC\n" +
                        "LIMIT 1)\n" +
                        "AND l.film_id NOT IN (SELECT film_id FROM likes\n" +
                        "WHERE user_id = ?)";
        List<Film> films = jdbcTemplate.query(sql, FilmDbStorage::createFilm, userId, userId, userId);
        films = FilmDbStorage.fillGenres(films, jdbcTemplate);
        for (Film film : films) {
            film.setDirectors(filmDbStorage.getFilmDirectors(film.getId()));
        }
        return films;
    }
}
