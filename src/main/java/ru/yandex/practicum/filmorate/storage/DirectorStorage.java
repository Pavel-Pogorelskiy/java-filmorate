package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Director get(int genreId);

    Collection<Director> findAll();

    Director create(Director director);

    Director put(Director director);

    void delete(int id);

    boolean isRegistered(int id);
}
