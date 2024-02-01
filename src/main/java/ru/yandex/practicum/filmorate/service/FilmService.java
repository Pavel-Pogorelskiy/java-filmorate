package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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

    public void addLike(int filmId, int userId) {
        userStorage.validateId(userId);
        filmStorage.validateId(filmId);
        likesStorage.addLikeFilm(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        userStorage.validateId(userId);
        filmStorage.validateId(filmId);
        likesStorage.removeLikeFilm(filmId, userId);
    }

    public List<Film> getFilms(int count) {
        return likesStorage.getFilms(count);
    }

    public List<Film> getFilteredFilms(Integer count, Integer genreId, Integer year) {

        if (genreId < 0) {
            genreId = 0;
        }

        if (year < 0) {
            year = 0;
        }

        if (genreId != 0 && year == 0) {
            return likesStorage.getFilmsFilteredByGenre(count, genreId);
        } else if (genreId == 0 && year != 0) {
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
}
