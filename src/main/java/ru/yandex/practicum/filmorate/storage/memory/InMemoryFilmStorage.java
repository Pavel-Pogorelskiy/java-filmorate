package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Component
public class InMemoryFilmStorage extends InMemoryBaseStorage<Film> implements FilmStorage {
    @Override
    public List<Film> getFilmsByDirector(int directorId) {
        return null;
    }
}
