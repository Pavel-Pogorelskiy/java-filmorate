package ru.yandex.practicum.filmorate.storage;

public interface MarksStorage {
    void addMarkFilm(int filmId, int userId, int mark);

    void updateMarkFilm(int filmId, int userId, int mark);

    void removeMarkFilm(int filmId, int userId);
}
