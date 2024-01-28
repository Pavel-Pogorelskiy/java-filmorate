package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film data) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Map<String, Object> params = Map.of(
                "name", data.getName(),
                "description", data.getDescription(),
                "releaseDate", data.getReleaseDate(),
                "duration", data.getDuration(),
                "mpa", data.getMpa().getId());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        data.setId(id.intValue());
        if (data.getGenres().size() > 0) {
            data.setGenres(addGenreDb(data.getGenres(), data.getId()));
        }
        return data;
    }

    @Override
    public Film uptade(Film data) {
        validateId(data.getId());
        jdbcTemplate.update(
                "update films Set name = ?, description = ?, " +
                        "releaseDate = ?, duration = ?, mpa = ? " +
                        "Where film_id = ?",
                data.getName(), data.getDescription(),
                data.getReleaseDate(), data.getDuration(),
                data.getMpa().getId(), data.getId());
        jdbcTemplate.update(
                "delete from genre_link where film_id = ?", data.getId());
        if (data.getGenres().size() > 0) {
            data.setGenres(addGenreDb(data.getGenres().stream()
                    .distinct()
                    .collect(Collectors.toList()), data.getId()));
        }
        return data;
    }

    @Override
    public Film get(int id) {
        validateId(id);
        List<Film> films = jdbcTemplate.query(
                "select f.film_id, f.name, f.description, " +
                        "f.releaseDate, f.duration, mp.mpa_id, " +
                        "mp.name as mpa_name from films as f " +
                        "join mpa as mp on f.mpa = mp.mpa_id " +
                        "where film_id = ?",
                FilmDbStorage::createFilm, id);
        if (films.size() != 1) {
            throw new NotFoundDataException("Пользователь больше или меньше одного");
        }
        List<Genre> genres = jdbcTemplate.query(
                "select gl.film_id, g.name, g.genre_id " +
                        "from genre_link gl " +
                        "join genre g on g.genre_id = gl.genre_id " +
                        "where gl.film_id in " +
                        "(select films.film_id from films where film_id = ?)",
                FilmDbStorage::createGenre, id);
        films.get(0).setGenres(genres);
        return films.get(0);
    }

    @SneakyThrows
    @Override
    public List<Film> getAll() {
        Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();
        List<Film> films = new ArrayList<>();
        ResultSet rs = statement.executeQuery(
                "select f.film_id, f.name, f.description, f.releaseDate, " +
                        "f.duration, mp.mpa_id, mp.name as mpa_name, gl.genre_id, " +
                        "g.name AS genre_name from films as f  " +
                        "join mpa as mp on f.mpa = mp.mpa_id " +
                        "left join genre_link gl ON gl.FILM_ID = f.FILM_ID " +
                        "left join genre g on g.genre_id = gl.genre_id " +
                        "order by f.film_id ASC");
        rs.next();
        if (rs.getRow() != 0) {
            while (!rs.isAfterLast()) {
                Film film = Film.builder()
                        .id(rs.getInt("film_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .duration(rs.getInt("duration"))
                        .releaseDate(rs.getDate("releaseDate").toLocalDate())
                        .mpa(Mpa.builder()
                                .id(rs.getInt("mpa_id"))
                                .name(rs.getString("mpa_name"))
                                .build())
                        .build();
                List<Genre> genres = new ArrayList<>();
                if (rs.getInt("genre_id") > 0) {
                    while (film.getId() == rs.getInt("film_id")) {
                        Genre genre = new Genre();
                        genre.setId(rs.getInt("genre_id"));
                        genre.setName(rs.getString("genre_name"));
                        genres.add(genre);
                        rs.next();
                        if (rs.isAfterLast()) {
                            break;
                        }
                    }
                } else {
                    rs.next();
                }
                film.setGenres(genres);
                films.add(film);
            }
        }
        return films;
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(
                "Delete From films Where film_id = ?", id);
    }

    public void validateId(int id) {
        List<Film> films = jdbcTemplate.query(
                "Select f.film_id, f.name, f.description, " +
                        "f.releaseDate, f.duration, mp.mpa_id, " +
                        "mp.name as mpa_name from films as f " +
                        "join mpa as mp on f.mpa = mp.mpa_id " +
                        "Where f.film_id = ?",
                FilmDbStorage::createFilm, id);
        if (films.size() == 0) {
            throw new NotFoundDataException("Фильма с id = " + id + " не существует");
        }
    }

    static Film createFilm(ResultSet rs, int RowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .mpa(Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build())
                .build();
    }

    static Genre createGenre(ResultSet rs, int RowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }

    private List<Genre> addGenreDb(final List<Genre> genres, int filmId) {
        jdbcTemplate.batchUpdate(
                "insert into genre_link (film_id, genre_id) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genres.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genres.size();
                    }
                });
        return genres;
    }
}