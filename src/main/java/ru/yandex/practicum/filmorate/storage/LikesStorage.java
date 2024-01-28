package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {
    void addLikeFilm(int filmId, int userId);

    void removeLikeFilm(int filmId, int userId);

    List<Film> getFilms(int count);
}
