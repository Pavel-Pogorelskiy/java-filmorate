package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.*;

import java.util.*;

@Service
public class FilmService {
    @Autowired
    private FilmDbStorage filmStorage;
    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private LikesDbStorage likesStorage;
    @Autowired
    private DirectorDbStorage directorStorage;

    @Autowired
    private EventService eventService;

    public void addLike(int filmId, int userId) {
        userStorage.validateId(userId);
        filmStorage.validateId(filmId);
        likesStorage.addLikeFilm(filmId, userId);

        eventService.addLikeEvent(userId, filmId, Event.EventOperation.ADD);
    }

    public void deleteLike(int filmId, int userId) {
        userStorage.validateId(userId);
        filmStorage.validateId(filmId);
        likesStorage.removeLikeFilm(filmId, userId);

        eventService.addLikeEvent(userId, filmId, Event.EventOperation.REMOVE);
    }

    public List<Film> getFilms(int count) {
        return likesStorage.getFilms(count);
    }

    public List<Film> getFilteredFilms(Integer count, Integer genreId, Integer year) {

        if (genreId != null && year == null) {
            return likesStorage.getFilmsFilteredByGenre(count, genreId);
        } else if (genreId == null && year != null) {
            return likesStorage.getFilmsFilteredByYear(count, year);
        } else {
            return likesStorage.getFilmsFilteredByGenreAndYear(count, genreId, year);
        }
    }

    public Collection<Film> getFilmsByDirector(int directorId, String sortBy) {
        if (directorStorage.isRegistered(directorId)) {
            List<Film> films = filmStorage.getFilmsByDirector(directorId);
            if (sortBy != null) {
                sortFilms(films, sortBy);
            }
            return films;
        } else {
            throw new NotFoundDataException("Режиссер с id = " + directorId + " не найден в списке.");
        }
    }

    private void sortFilms(List<Film> films, String sortBy) {
        switch (sortBy) {
            case "year":
                films.sort(Comparator.comparing(Film::getReleaseDate));
                break;
            case "likes":
                films.sort((f1, f2) -> f2.getLikes().size() - f1.getLikes().size());
                break;
            default:
                throw new IllegalArgumentException("Invalid sort parameter: " + sortBy);
        }
    }

    public Set<Film> getCommonFilms(int userId, int friendId) {
        return Set.copyOf(filmStorage.getCommonFilms(userId, friendId));
    }

    public List<Film> searchFilms(String query, List<String> by) {
        if (by.contains("director") && by.contains("title")) {
            return filmStorage.searchFilm(query, true, true);
        } else if (by.contains("director")) {
            return filmStorage.searchFilm(query, false, true);
        } else if (by.contains("title")) {
            return filmStorage.searchFilm(query, true, false);
        } else {
            throw new RuntimeException("Условие поиска задано неверно: необходимо указать title, director или оба варианта.");
        }
    }
}
