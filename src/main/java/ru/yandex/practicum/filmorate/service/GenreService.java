package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import java.util.List;

@Service
public class GenreService {
    @Autowired
    private GenreDbStorage genreStorage;

    public List <Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre get(int id) {
        return genreStorage.get(id);
    }
}