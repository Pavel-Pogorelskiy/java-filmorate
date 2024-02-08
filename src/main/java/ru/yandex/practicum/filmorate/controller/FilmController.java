package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateDateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.MarksService;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Validated
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @Autowired
    private MarksService marksService;

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

    @PostMapping(value = "/{id}/mark/{userId}")
    public void addMark(@PathVariable int id,@PathVariable int userId,
                        @RequestParam @Range(min = 1, max = 10) int mark) {
        marksService.addMarkFilm(id, userId, mark);
    }

    @PutMapping(value = "/{id}/mark/{userId}")
    public void updateMark(@PathVariable int id,@PathVariable int userId,
                            @RequestParam @Range(min = 1, max = 10) int mark) {

        marksService.updateMarkFilm(id, userId, mark);
    }

    @DeleteMapping("/{id}/mark/{userId}")
    public void deleteMark(@PathVariable int id, @PathVariable int userId) {
        marksService.removeMarkFilm(id, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10")
                                          Integer count,
                                      @RequestParam(value = "genreId", required = false)
                                      @Positive Integer genreId,
                                      @Valid @RequestParam(value = "year", required = false)
                                      @Min(1895) Integer year) {

        if (genreId == null && year == null) {
            return filmService.getFilms(count);
        } else {
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
                                               @RequestParam(value = "sortBy", required = false) SortType sortBy) {
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

        if (query == null || by == null || by.size() > 2) {
            throw new RuntimeException("Условие поиска задано неверно!");
        }

        if (by.contains("director") && by.contains("title")) {
            return filmService.searchFilm(query, true, true);
        } else if (by.contains("director")) {
            return filmService.searchFilm(query, false, true);
        } else if (by.contains("title")) {
            return filmService.searchFilm(query, true, false);
        } else {
            throw new RuntimeException("Условие поиска задано неверно: необходимо указать " +
                    "title, director или оба варианта.");
        }
    }
}
