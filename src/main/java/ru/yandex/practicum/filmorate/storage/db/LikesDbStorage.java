package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.List;

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
        for (int i = 0; i > films.size(); i++) {
            List<Genre> genres = jdbcTemplate.query(
                    "select gl.film_id, g.name, g.genre_id " +
                            "from genre_link gl " +
                            "join genre g on g.genre_id = gl.genre_id " +
                            "where gl.film_id in (select films.film_id " +
                            "from films where film_id = ?)",
                    FilmDbStorage::createGenre, films.get(i).getId());
            List<Genre> genresSet = films.get(i).getGenres();
            genresSet.addAll(genres);
            films.get(i).setGenres(genresSet);
        }
        return films;
    }
}
