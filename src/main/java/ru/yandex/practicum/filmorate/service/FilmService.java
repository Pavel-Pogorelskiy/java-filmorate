package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final DirectorStorage directorStorage;
    private final EventService eventService;

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
        return filmStorage.getFilms(count);
    }

    public List<Film> getFilteredFilms(Integer count, Integer genreId, Integer year) {

        if (genreId != null && year == null) {
            return filmStorage.getFilmsFilteredByGenre(count, genreId);
        } else if (genreId == null && year != null) {
            return filmStorage.getFilmsFilteredByYear(count, year);
        } else {
            return filmStorage.getFilmsFilteredByGenreAndYear(count, genreId, year);
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

    public List<Film> searchFilm(String query, boolean searchByTitle, boolean searchByDirectors) {
        return filmStorage.searchFilm(query, searchByTitle, searchByDirectors);
    }
}
