package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateDateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

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
    public List<Film> getPopularFilms(@RequestParam(value = "count",required = false,defaultValue = "10") Integer count) {
        return filmService.getFilms(count);
    }

    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATE_FIRST_RELEASE)) {
            throw new ValidateDateException("Неверно указана дата релиза фильма. Минимальное значение 28.12.1895");
        }
    }

    @DeleteMapping(value = "/{id}")
    public void removeFilm(@PathVariable int id) {
        log.info("Удаление фильма с id = {} ", id);
        filmStorage.delete(id);
    }
}
