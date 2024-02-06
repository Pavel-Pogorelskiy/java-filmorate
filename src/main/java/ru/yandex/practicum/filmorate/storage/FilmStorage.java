package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends AbstractStorage<Film> {
    List<Film> getFilmsByDirector(int directorId);

    void validateId(int filmId);

    List<Film> getFilmsFilteredByGenre(int limit, int genreId);

    List<Film> getFilmsFilteredByYear(int limit, int year);

    List<Film> getFilmsFilteredByGenreAndYear(int limit, int genreId, int year);

    List<Film> getFilms(int count);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> searchFilm(String query, boolean searchByTitle, boolean searchByDirectors);

    List<Film> recommendationsFilms(int userId);
}
