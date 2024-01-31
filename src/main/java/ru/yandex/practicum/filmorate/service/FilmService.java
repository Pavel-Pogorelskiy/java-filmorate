package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class FilmService {
    @Autowired
    private FilmDbStorage filmStorage;
    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private LikesDbStorage likesStorage;

    public void addLike(int filmId, int userId) {
        userStorage.validateId(userId);
        filmStorage.validateId(filmId);
        likesStorage.addLikeFilm(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        userStorage.validateId(userId);
        filmStorage.validateId(filmId);
        likesStorage.removeLikeFilm(filmId, userId);
    }

    public List<Film> getFilms(int count) {
        return likesStorage.getFilms(count);
    }

    public Set<Film> getCommonFilms(int userId, int friendId) throws SQLException {
        Set<Film> commonFilms = new TreeSet<>(Comparator.comparing(Film::countLikes).reversed());
        commonFilms.addAll(filmStorage.getCommonFilms(userId, friendId));
        return commonFilms;
    }
}
