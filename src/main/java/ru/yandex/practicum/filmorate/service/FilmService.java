package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserStorage userStorage;

    public void addLike(int id, int userId) {
        User user = userStorage.get(userId);
        Film film = filmStorage.get(id);
        Set<Integer> filmLikes = film.getLikes();
        filmLikes.add(user.getId());
        film.setLikes(filmLikes);
        filmStorage.uptade(film);
    }

    public void deleteLike(int id, int userId) {
        User user = userStorage.get(userId);
        Film film = filmStorage.get(id);
        Set<Integer> filmLikes = film.getLikes();
        filmLikes.remove(user.getId());
        film.setLikes(filmLikes);
        filmStorage.uptade(film);
    }

    public List<Film> getFilms(int count) {
        List<Film> filmPopular = filmStorage.getAll().stream()
                .sorted((o1, o2) ->
                        o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
        return filmPopular;
    }
}
