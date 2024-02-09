package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends AbstractStorage<Film> {
    List<Film> getFilmsByDirector(int directorId);

    void validateId(int filmId);

    List<Film> getPopularFilmsFilteredByGenre(int limit, int genreId);

    List<Film> getPopularFilmsFilteredByYear(int limit, int year);

    List<Film> getPopularFilmsFilteredByGenreAndYear(int limit, int genreId, int year);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> searchFilm(String query, boolean searchByTitle, boolean searchByDirectors);

    List<Film> recommendationsFilms(int userId);

    List<Film> getPopularFilms(int count);
}
