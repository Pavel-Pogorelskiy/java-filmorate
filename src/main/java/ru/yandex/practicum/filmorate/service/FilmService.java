package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

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
}
