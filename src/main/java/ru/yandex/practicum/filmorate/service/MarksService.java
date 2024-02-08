package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.MarksStorage;

@Service
@RequiredArgsConstructor
public class MarksService {
    private final MarksStorage marksStorage;

    public void addMarkFilm(int filmId, int userId, int mark) {
        marksStorage.addMarkFilm(filmId, userId, mark);
    }

    public void updateMarkFilm(int filmId, int userId, int mark) {
        marksStorage.updateMarkFilm(filmId, userId, mark);
    }

    public void removeMarkFilm(int filmId, int userId) {
        marksStorage.removeMarkFilm(filmId, userId);
    }
}