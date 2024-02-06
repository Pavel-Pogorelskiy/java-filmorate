package ru.yandex.practicum.filmorate.storage;

public interface LikesStorage {
    void addLikeFilm(int filmId, int userId);

    void removeLikeFilm(int filmId, int userId);
}
