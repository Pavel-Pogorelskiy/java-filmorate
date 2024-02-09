package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.SortType;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;


import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final EventService eventService;

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getPopularFilteredFilms(Integer count, Integer genreId, Integer year) {

        if (genreId != null && year == null) {
            return filmStorage.getPopularFilmsFilteredByGenre(count, genreId);
        } else if (genreId == null && year != null) {
            return filmStorage.getPopularFilmsFilteredByYear(count, year);
        } else {
            return filmStorage.getPopularFilmsFilteredByGenreAndYear(count, genreId, year);
        }
    }

    public Collection<Film> getFilmsByDirector(int directorId, SortType sortBy) {
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

    private void sortFilms(List<Film> films, SortType sortBy) {
        switch (sortBy) {
            case YEAR:
                films.sort(Comparator.comparing(Film::getReleaseDate));
                break;
            case MARK:
                films.sort(Comparator.comparing(Film::getMark).reversed());
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
