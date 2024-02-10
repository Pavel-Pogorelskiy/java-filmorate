package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Director;
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
        if (data.getDirectors().size() > 0) {
            addFilmDirector(data);
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

        deleteFilmDirectors(data.getId());
        if (isDirectorsRegistered(data)) {
            addFilmDirector(data);
        }
        data.setDirectors(getFilmDirectors(data.getId()));
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
        films.get(0).setDirectors(getFilmDirectors(id));
        return films.get(0);
    }

    @SneakyThrows
    @Override
    public List<Film> getAll() {
        List<Film> films = jdbcTemplate.query(
                "select f.film_id, f.name, " +
                        "f.description, f.releaseDate, f.duration, " +
                        "m.mpa_id, m.name as mpa_name from films as f " +
                        "join mpa m on f.mpa = m.mpa_id",
                FilmDbStorage::createFilm);
        FilmDbStorage.fillGenres(films, jdbcTemplate);
        return FilmDbStorage.fillDirectors(films, jdbcTemplate);
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(
                "Delete From films Where film_id = ?", id);
    }

    @Override
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

    private Film createFilmWithMark(ResultSet rs, int RowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build())
                .mark(rs.getFloat("mark"))
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

    public void addFilmDirector(Film film) {
        List<Director> directors = List.copyOf(film.getDirectors());
        if (!directors.isEmpty()) {
            String sqlQuery = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, directors.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return directors.size();
                }
            });
        }
    }

    public void deleteFilmDirectors(int filmId) {
        String sqlQuery = "DELETE FROM films_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private boolean isDirectorsRegistered(Film film) {
        for (Director director : film.getDirectors()) {
            if (!isDirectorRegistered(director.getId())) {
                throw new NotFoundDataException("Режиссер с id = " + director.getId() + " не найден в списке.");
            }
        }
        return true;
    }

    public boolean isDirectorRegistered(int directorId) {
        String sqlQuery = "SELECT * FROM directors WHERE director_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlQuery, directorId);
        return genreRow.next();
    }

    public Set<Director> getFilmDirectors(int filmId) {
        Set<Director> filmDirector = new HashSet<>();
        String sqlQuery = "SELECT * FROM directors WHERE director_id IN " +
                "(SELECT director_id FROM films_directors WHERE film_id = ?)";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        while (rs.next()) {
            filmDirector.add(directorRowMap(rs));
        }
        return filmDirector;
    }

    private Director directorRowMap(SqlRowSet rs) {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }

    @SneakyThrows
    public List<Film> getFilmsByDirector(int directorId) {
        String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate,  " +
                "f.duration, mp.mpa_id, mp.name as mpa_name, AVG(m.mark) AS mark " +
                "FROM films as f JOIN mpa as mp on f.mpa = mp.mpa_id " +
                "LEFT JOIN marks AS m on f.film_id = m.film_id " +
                "WHERE f.film_id IN (SELECT film_id FROM films_directors WHERE director_id = ?) " +
                "GROUP BY f.film_id";
        List<Film> films = jdbcTemplate.query(sql, this::createFilmWithMark, directorId);
        FilmDbStorage.fillGenres(films, jdbcTemplate);
        return FilmDbStorage.fillDirectors(films, jdbcTemplate);
    }

    static Map<Integer, List<Genre>> createGenres(ResultSet rs, int RowNum) throws SQLException {
        Map<Integer, List<Genre>> genreMap = new HashMap<>();
        boolean done = false;
        do {
            int filmId = rs.getInt("film_id");
            List<Genre> genreList = new ArrayList<>();
            do {
                int genreId = rs.getInt("genre_id");
                if (genreId == 0) {
                    done = !rs.next();
                    break;
                }
                Genre genre = Genre.builder()
                        .id(genreId)
                        .name(rs.getString("name"))
                        .build();
                genreList.add(genre);
                done = !rs.next();
            } while (!done && (rs.getInt("film_id") == filmId));
            genreMap.put(filmId, genreList);
        } while (!done);
        return genreMap;
    }

    static List<Film> fillGenres(List<Film> films, JdbcTemplate jdbcTemplate) {
        if (films.isEmpty()) {
            return films;
        }
        StringBuilder filmIds = new StringBuilder();
        for (Film film : films) {
            filmIds.append(film.getId()).append(",");
        }
        filmIds.deleteCharAt(filmIds.length() - 1);
        String sql =
                "SELECT gl.film_id, g.genre_id, g.name " +
                        "FROM genre_link gl " +
                        "JOIN genre g ON g.genre_id = gl.genre_id " +
                        "WHERE gl.film_id IN (" + filmIds.toString() + ");";
        try {
            Map<Integer, List<Genre>> genres = jdbcTemplate.queryForObject(
                    sql, FilmDbStorage::createGenres);
            return films.stream()
                    .map(film -> {
                        if (genres.get(film.getId()) == null) {
                        film.setGenres(new ArrayList<Genre>());
                    } else {
                        film.setGenres(genres.get(film.getId()));
                    }
                        return film;
                    })
                    .collect(Collectors.toList());
        } catch (EmptyResultDataAccessException e) {
            return films;
        }
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate,  " +
                "f.duration, mp.mpa_id, mp.name as mpa_name, AVG(m.mark) AS mark " +
                "FROM films as f JOIN mpa as mp on f.mpa = mp.mpa_id " +
                "LEFT JOIN marks AS m on f.film_id = m.film_id " +
                "WHERE m.user_id  = ? " +
                "AND f.film_id IN ( " +
                "SELECT m.film_id " +
                "FROM marks as m " +
                "WHERE user_id = ? ) " +
                "GROUP BY f.film_id " +
                "ORDER BY mark desc";
        List<Film> films = jdbcTemplate.query(sql, this::createFilmWithMark, userId, friendId);
        FilmDbStorage.fillGenres(films, jdbcTemplate);
        return FilmDbStorage.fillDirectors(films, jdbcTemplate);
    }

    public List<Film> searchFilm(String query, boolean searchByTitle, boolean searchByDirector) {
        String sqlStart = "SELECT f.film_id, f.name, f.description, f.releaseDate, " +
                "f.duration, mp.mpa_id, mp.name as mpa_name, AVG(m.mark) AS mark " +
                "FROM films as f " +
                "JOIN mpa as mp on f.mpa = mp.mpa_id " +
                "LEFT JOIN films_directors as fd on f.film_id = fd.film_id " +
                "LEFT JOIN directors as d on fd.director_id = d.director_id " +
                "LEFT JOIN marks AS m on f.film_id = m.film_id " +
                "WHERE ";

        String searchQuery;
        String searchByTitleQuery = "LOWER(f.name) LIKE '%" + query.toLowerCase() + "%'";
        String searchByDirectorQuery = "LOWER(d.name) LIKE '%" + query.toLowerCase() + "%'";

        if (searchByTitle && searchByDirector) {
            searchQuery = searchByTitleQuery + " OR " + searchByDirectorQuery;
        } else if (searchByTitle) {
            searchQuery = searchByTitleQuery;
        } else {
            searchQuery = searchByDirectorQuery;
        }

        String sqlFinish = "GROUP BY f.film_id " +
                "ORDER BY mark DESC";

        List<Film> films = jdbcTemplate.query(sqlStart + searchQuery + sqlFinish, this::createFilmWithMark);
        FilmDbStorage.fillGenres(films, jdbcTemplate);
        return FilmDbStorage.fillDirectors(films, jdbcTemplate);
    }

    @Override
    public List<Film> getPopularFilms(int count) {

        List<Film> films = jdbcTemplate.query(
                "SELECT f.film_id, f.name, " +
                    "f.description, f.releaseDate, f.duration, " +
                    "mp.mpa_id, mp.name as mpa_name, mark FROM " +
                    "(SELECT m.film_id as film_id, " +
                    "AVG(m.mark) AS mark FROM marks AS m " +
                    "GROUP BY m.film_id) AS r " +
                    "RIGHT JOIN films AS f ON r.film_id = f.film_id " +
                    "JOIN mpa AS mp ON f.mpa = mp.mpa_id  " +
                    "ORDER BY mark DESC LIMIT ?;",
                this::createFilmWithMark, count);

        FilmDbStorage.fillGenres(films, jdbcTemplate);
        return FilmDbStorage.fillDirectors(films, jdbcTemplate);
    }

    @Override
    public List<Film> getPopularFilmsFilteredByYear(int count, int year) {

        String sql =    "SELECT f.film_id, f.name, " +
                        "f.description, f.releaseDate, f.duration, " +
                        "mp.mpa_id, mp.name as mpa_name, mark FROM " +
                        "(SELECT m.film_id as film_id, " +
                        "AVG(m.mark) AS mark FROM marks AS m " +
                        "GROUP BY m.film_id) AS r " +
                        "RIGHT JOIN films AS f ON r.film_id = f.film_id " +
                        "JOIN mpa AS mp ON f.mpa = mp.mpa_id  " +
                        "WHERE EXTRACT(YEAR FROM f.releaseDate) = ? "  +
                        "ORDER BY mark DESC LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sql, this::createFilmWithMark, year, count);
        FilmDbStorage.fillGenres(films, jdbcTemplate);
        return FilmDbStorage.fillDirectors(films, jdbcTemplate);
    }

    public List<Film> getPopularFilmsFilteredByGenre(int count, int genreId) {

        String sql =    "SELECT mp.* FROM " +

                        "(SELECT f.film_id, f.name, " +
                        "f.description, f.releaseDate, f.duration, " +
                        "mp.mpa_id, mp.name as mpa_name, mark FROM " +
                        "(SELECT m.film_id as film_id, " +
                        "AVG(m.mark) AS mark FROM marks AS m " +
                        "GROUP BY m.film_id) AS r " +
                        "RIGHT JOIN films AS f ON r.film_id = f.film_id " +
                        "JOIN mpa AS mp ON f.mpa = mp.mpa_id) AS mp " +

                        "WHERE mp.film_id IN " +
                        "(SELECT film_id FROM genre_link " +
                        "WHERE genre_id = ?) " +
                        "ORDER BY mp.mark DESC " +
                        "LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sql, this::createFilmWithMark, genreId, count);
        FilmDbStorage.fillGenres(films, jdbcTemplate);
        return FilmDbStorage.fillDirectors(films, jdbcTemplate);
    }

    @Override
    public List<Film> getPopularFilmsFilteredByGenreAndYear(int count, int genreId, int year) {

        String sql =
                "SELECT mp.* FROM " +

                        "(SELECT f.film_id, f.name, " +
                        "f.description, f.releaseDate, f.duration, " +
                        "mp.mpa_id, mp.name as mpa_name, mark FROM " +
                        "(SELECT m.film_id as film_id, " +
                        "AVG(m.mark) AS mark FROM marks AS m " +
                        "GROUP BY m.film_id) AS r " +
                        "RIGHT JOIN films AS f ON r.film_id = f.film_id " +
                        "JOIN mpa AS mp ON f.mpa = mp.mpa_id  " +
                        "WHERE EXTRACT(YEAR FROM f.releaseDate) = ?) AS mp "  +

                        "WHERE mp.film_id IN " +
                        "(SELECT film_id FROM genre_link " +
                        "WHERE genre_id = ?) " +
                        "ORDER BY mp.mark DESC " +
                        "LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sql, this::createFilmWithMark, year, genreId, count);
        FilmDbStorage.fillGenres(films, jdbcTemplate);
        return FilmDbStorage.fillDirectors(films, jdbcTemplate);
    }

    @Override
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
        FilmDbStorage.fillGenres(films, jdbcTemplate);
        return FilmDbStorage.fillDirectors(films, jdbcTemplate);
    }

    static Map<Integer, Set<Director>> createDirectors(ResultSet rs, int RowNum) throws SQLException {
        Map<Integer, Set<Director>> directorMap = new HashMap<>();
        boolean done = false;
        do {
            int filmId = rs.getInt("film_id");
            Set<Director> directorSet = new HashSet<>();
            do {
                int directorId = rs.getInt("director_id");
                if (directorId == 0) {
                    done = !rs.next();
                    break;
                }
                Director director = Director.builder()
                        .id(directorId)
                        .name(rs.getString("name"))
                        .build();
                directorSet.add(director);
                done = !rs.next();
            } while (!done && (rs.getInt("film_id") == filmId));
            directorMap.put(filmId, directorSet);
        } while (!done);
        return directorMap;
    }

    static List<Film> fillDirectors(List<Film> films, JdbcTemplate jdbcTemplate) {
        if (films.isEmpty()) {
            return films;
        }
        StringBuilder filmIds = new StringBuilder();
        for (Film film : films) {
            filmIds.append(film.getId()).append(",");
        }
        filmIds.deleteCharAt(filmIds.length() - 1);
        String sql =
                "SELECT fd.film_id, d.director_id, d.name " +
                        "FROM films_directors AS fd " +
                        "JOIN directors AS d ON fd.director_id = d.director_id " +
                        "WHERE fd.film_id IN (" + filmIds.toString() + ");";
        try {
            Map<Integer, Set<Director>> directors = jdbcTemplate.queryForObject(
                    sql, FilmDbStorage::createDirectors);
            return films.stream()
                    .peek(film -> {
                        if (directors.get(film.getId()) == null) {
                            film.setDirectors(new HashSet<>());
                        } else {
                            film.setDirectors(directors.get(film.getId()));
                        }
                    })
                    .collect(Collectors.toList());
        } catch (EmptyResultDataAccessException e) {
            return films;
        }
    }
}