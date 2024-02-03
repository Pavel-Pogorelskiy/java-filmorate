package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidateDateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmService filmService;
    @Autowired
    private FilmDbStorage filmStorage;
    private static final LocalDate DATE_FIRST_RELEASE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> getAll() {
        log.info("Получение списка всех фильмов  {}", filmStorage.getAll());
        return filmStorage.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        log.info("Добавление фильма {}", film);
        return filmStorage.create(film);
    }

    @PutMapping
    public Film uptade(@Valid @RequestBody Film film) {
        log.info("Обновление фильма {}", film);
        return filmStorage.uptade(film);
    }

    @GetMapping(value = "/{id}")
    public Film getFilmId(@PathVariable int id) {
        log.info("Получение фильма {}", filmStorage.get(id));
        return filmStorage.get(id);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable int id,@PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id,@PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                      @RequestParam(value = "genreId", required = false) Integer genreId,
                                      @RequestParam(value = "year", required = false) Integer year) {

        if (genreId == null && year == null) {
            return filmService.getFilms(count);
        } else {

            if (genreId != null && (genreId < 1 || genreId > 6)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Неверно указан жанр фильма. Значение должно быть в диапазоне от 1 до 6 включительно");
            }

            if (year != null && (year < DATE_FIRST_RELEASE.getYear() || year > LocalDate.now().getYear())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Неверно указан год релиза фильма. Значение должно быть в диапазоне от 1895 до сего года включительно");
            }

            return filmService.getFilteredFilms(count, genreId, year);
        }
    }

    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATE_FIRST_RELEASE)) {
            throw new ValidateDateException("Неверно указана дата релиза фильма. Минимальное значение 28.12.1895");
        }
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable(value = "directorId") int directorId,
                                               @RequestParam(value = "sortBy", required = false) String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @DeleteMapping(value = "/{id}")
    public void removeFilm(@PathVariable int id) {
        log.info("Удаление фильма с id = {} ", id);
        filmStorage.delete(id);
    }

    @GetMapping("/common")
    public Set<Film> getCommonFilms(@Validated @RequestParam @Min(1) Integer userId,
                                    @Validated @RequestParam @Min(1) Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query,
                                 @RequestParam List<String> by) {
        log.info("Получен запрос на поиск фильма. Поисковой запрос {}, искать по {}.", query, by);
        return filmService.searchFilms(query, by);
    }
}
